package com.fsb.Service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsb.Mapper.UserMapper;
import com.fsb.Service.PlatformIdentityService;
import com.fsb.auth.AiContextSignature;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.PlatformBindingConfirmRequest;
import com.fsb.pojo.VO.PlatformBindingTokenVO;
import com.fsb.pojo.VO.PlatformBindingVO;
import com.fsb.pojo.VO.AiResolvedIdentityVO;
import com.fsb.pojo.entity.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PlatformIdentityServiceImpl implements PlatformIdentityService {
    private static final long BIND_TOKEN_TTL_SECONDS = 300;
    private static final String KEY_PREFIX = "cs:identity:";
    private static final String TOKEN_PREFIX = "cs:bind:token:";
    private static final String IDEMPOTENCY_PREFIX = "cs:idem:bind:";
    private static final DefaultRedisScript<Long> MIGRATE_REVERSE_MEMBER_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[2]) == ARGV[3] then "
                    + "redis.call('sadd', KEYS[1], ARGV[2]); "
                    + "redis.call('hset', KEYS[3], 'encryptedExternalId', ARGV[4]); end; "
                    + "return redis.call('srem', KEYS[1], ARGV[1])",
            Long.class);
    private static final DefaultRedisScript<Long> MIGRATE_LEGACY_IDENTITY_SCRIPT = new DefaultRedisScript<>(
            "local oldOwner = redis.call('get', KEYS[1]); "
                    + "local newOwner = redis.call('get', KEYS[2]); "
                    + "if not oldOwner then return 0 end; "
                    + "if newOwner and newOwner ~= oldOwner then return -1 end; "
                    + "if not newOwner then redis.call('set', KEYS[2], oldOwner) end; "
                    + "redis.call('sadd', ARGV[2] .. oldOwner, ARGV[1]); "
                    + "redis.call('srem', ARGV[2] .. oldOwner, ARGV[3]); "
                    + "local values = redis.call('hgetall', KEYS[3]); "
                    + "if #values > 0 and redis.call('exists', KEYS[4]) == 0 then "
                    + "redis.call('hmset', KEYS[4], unpack(values)); "
                    + "local ttl = redis.call('pttl', KEYS[3]); if ttl >= 0 then redis.call('pexpire', KEYS[4], ttl) end; end; "
                    + "redis.call('hset', KEYS[4], 'encryptedExternalId', ARGV[4]); "
                    + "local oldVersion = redis.call('get', KEYS[5]); "
                    + "local newVersion = redis.call('get', KEYS[6]); "
                    + "if oldVersion and not newVersion then redis.call('set', KEYS[6], oldVersion) end; "
                    + "redis.call('del', KEYS[1], KEYS[3], KEYS[5]); return 1",
            Long.class);
    private static final DefaultRedisScript<Long> BIND_SCRIPT = new DefaultRedisScript<>(
                    "local idemValue = redis.call('get', KEYS[6]); "
                    + "if idemValue then if idemValue == ARGV[5] then "
                    + "if redis.call('get', KEYS[1]) == ARGV[1] then return tonumber(redis.call('hget', KEYS[3], 'version') or '1') else return -3 end "
                    + "else return -2 end end; "
                    + "local tokenValue = redis.call('get', KEYS[5]); "
                    + "if not tokenValue or tokenValue ~= ARGV[4] then return 0 end; "
                    + "local current = redis.call('get', KEYS[1]); "
                    + "if current and current ~= ARGV[1] then return -1 end; "
                    + "if current == ARGV[1] then "
                    + "local version = tonumber(redis.call('hget', KEYS[3], 'version') or '1'); "
                    + "redis.call('set', KEYS[6], ARGV[5], 'EX', 86400); redis.call('del', KEYS[5]); return version end; "
                    + "local version = redis.call('incr', KEYS[4]); "
                    + "redis.call('set', KEYS[1], ARGV[1]); redis.call('sadd', KEYS[2], ARGV[2]); "
                    + "redis.call('hset', KEYS[3], 'customerId', ARGV[1], 'version', version, 'boundAt', ARGV[3], 'status', 'active', 'encryptedExternalId', ARGV[6]); "
                    + "redis.call('set', KEYS[6], ARGV[5], 'EX', 86400); redis.call('del', KEYS[5]); return version",
            Long.class);
    private static final DefaultRedisScript<Long> UNBIND_SCRIPT = new DefaultRedisScript<>(
            "local current = redis.call('get', KEYS[1]); "
                    + "if not current then return 0 end; "
                    + "if current ~= ARGV[1] then return -1 end; "
                    + "redis.call('del', KEYS[1]); redis.call('srem', KEYS[2], ARGV[2]); "
                    + "local version = redis.call('incr', KEYS[4]); "
                    + "redis.call('hset', KEYS[3], 'version', version, 'status', 'revoked'); "
                    + "redis.call('expire', KEYS[3], 86400); return version",
            Long.class);
    private static final DefaultRedisScript<Long> MOVE_SCRIPT = new DefaultRedisScript<>(
            "local current = redis.call('get', KEYS[1]); "
                    + "if not current then return 0 end; "
                    + "if current ~= ARGV[1] then return -1 end; "
                    + "redis.call('srem', KEYS[2], ARGV[2]); redis.call('sadd', KEYS[3], ARGV[2]); "
                    + "redis.call('set', KEYS[1], ARGV[3]); "
                    + "local version = redis.call('incr', KEYS[5]); "
                    + "redis.call('hset', KEYS[4], 'customerId', ARGV[3], 'version', version, 'boundAt', ARGV[4], 'status', 'active'); "
                    + "return version",
            Long.class);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    private final AiContextSignature contextSignature;
    private final SecureRandom secureRandom = new SecureRandom();

    public PlatformIdentityServiceImpl(StringRedisTemplate redisTemplate,
                                       ObjectMapper objectMapper,
                                       UserMapper userMapper,
                                       AiContextSignature contextSignature) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.userMapper = userMapper;
        this.contextSignature = contextSignature;
    }

    @Override
    public PlatformBindingTokenVO startBinding(Long customerId, String platform) {
        platform = normalizePlatform(platform);
        validatePlatform(platform);
        User user = customerId == null ? null : userMapper.getById(customerId);
        if (user == null || user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalArgumentException("商城用户不存在");
        }

        String token = randomToken();
        BindingToken value = new BindingToken(customerId, platform, Instant.now().plusSeconds(BIND_TOKEN_TTL_SECONDS));
        putJson(TOKEN_PREFIX + contextSignature.digest(token), value, Duration.ofSeconds(BIND_TOKEN_TTL_SECONDS));

        PlatformBindingTokenVO result = new PlatformBindingTokenVO();
        result.setPlatform(platform);
        result.setToken(token);
        result.setExpiresAt(value.expiresAt());
        return result;
    }

    @Override
    public PlatformBindingVO confirmBinding(PlatformBindingConfirmRequest request) {
        String platform = normalizePlatform(request.getPlatform());
        String externalUserId = normalizeExternalId(request.getExternalUserId());
        validatePlatform(platform);

        String idempotencyKey = request.getIdempotencyKey().trim();
        if (idempotencyKey.length() > 100) {
            throw new IllegalArgumentException("幂等键过长");
        }
        String identityHash = identityKey(platform, externalUserId);
        migrateLegacyIdentity(platform, externalUserId);
        String reverseMember = reverseMember(platform, externalUserId);
        String encryptedExternalId = contextSignature.encryptPlatformExternalId(platform, externalUserId);
        String idemKey = IDEMPOTENCY_PREFIX + identityHash + ":" + idempotencyKey;
        String token = request.getToken().trim();
        String tokenDigest = contextSignature.digest(token);
        String existing = redisTemplate.opsForValue().get(idemKey);
        String expectedPrefix = identityHash + ":" + tokenDigest + ":";
        Long retryCustomerId = null;
        if (existing != null) {
            if (!existing.startsWith(expectedPrefix)) {
                throw new AuthException(409, "IDEMPOTENCY_CONFLICT: 幂等键已用于其他请求");
            }
            try {
                retryCustomerId = Long.valueOf(existing.substring(expectedPrefix.length()));
            } catch (NumberFormatException ex) {
                throw new IllegalStateException("绑定幂等记录格式错误", ex);
            }
        }
        BindingToken bindingToken = readToken(token);
        boolean retry = retryCustomerId != null;
        if (!retry && (bindingToken == null || bindingToken.expiresAt().isBefore(Instant.now()))) {
            throw new IllegalArgumentException("绑定凭证已过期或无效");
        }
        if (!retry && !platform.equals(bindingToken.platform())) {
            throw new IllegalArgumentException("绑定凭证与平台不匹配");
        }
        if (retry) {
            bindingToken = new BindingToken(retryCustomerId, platform, Instant.MAX);
        }
        migrateLegacyReverseMember(bindingToken.customerId(), platform, externalUserId);

        String current = redisTemplate.opsForValue().get(forwardKey(platform, externalUserId));
        if (current != null && !current.equals(String.valueOf(bindingToken.customerId()))) {
            throw new AuthException(409, "IDENTITY_CONFLICT: 平台身份已绑定其他商城账号");
        }
        String idemValue = identityHash + ":" + tokenDigest + ":" + bindingToken.customerId();
        Long version = redisTemplate.execute(BIND_SCRIPT,
                List.of(forwardKey(platform, externalUserId), reverseKey(bindingToken.customerId()),
                        metaKey(platform, externalUserId), versionKey(platform, externalUserId),
                        TOKEN_PREFIX + tokenDigest, idemKey),
                String.valueOf(bindingToken.customerId()), reverseMember,
                Instant.now().toString(), serializeToken(bindingToken), idemValue, encryptedExternalId);
        if (Long.valueOf(-1L).equals(version)) {
            throw new AuthException(409, "IDENTITY_CONFLICT: 平台身份已绑定其他商城账号");
        }
        if (Long.valueOf(-2L).equals(version)) {
            throw new AuthException(409, "IDEMPOTENCY_CONFLICT: 幂等键已用于其他请求");
        }
        if (Long.valueOf(-3L).equals(version)) {
            throw new AuthException(409, "BINDING_REVOKED: 该绑定已解绑，请重新生成绑定码");
        }
        if (Long.valueOf(0L).equals(version)) {
            throw new IllegalArgumentException("绑定凭证已过期或已使用");
        }
        return readBinding(platform, externalUserId, bindingToken.customerId());
    }

    @Override
    public List<PlatformBindingVO> listBindings(Long customerId) {
        if (customerId == null) {
            return List.of();
        }
        var members = redisTemplate.opsForSet().members(reverseKey(customerId));
        if (members == null || members.isEmpty()) {
            return List.of();
        }
        List<PlatformBindingVO> result = new ArrayList<>();
        for (String member : members) {
            int separator = member.indexOf(':');
            if (separator <= 0 || separator == member.length() - 1) {
                continue;
            }
            String platform = member.substring(0, separator);
            String identityReference = member.substring(separator + 1);
            if (!"telegram".equals(platform) && !"qq".equals(platform)) continue;
            String externalUserId;
            if (identityReference.startsWith("v2:")) {
                externalUserId = contextSignature.decryptPlatformExternalId(platform, identityReference);
                migrateLegacyReverseMember(customerId, platform, externalUserId);
                redisTemplate.opsForSet().remove(reverseKey(customerId), member);
            } else {
                Object encryptedValue = redisTemplate.opsForHash()
                        .get(metaKeyByHash(identityReference), "encryptedExternalId");
                if (encryptedValue != null) {
                    externalUserId = contextSignature.decryptPlatformExternalId(platform,
                            String.valueOf(encryptedValue));
                } else {
                    externalUserId = identityReference;
                    migrateLegacyIdentity(platform, externalUserId);
                    migrateLegacyReverseMember(customerId, platform, externalUserId);
                }
            }
            migrateLegacyIdentity(platform, externalUserId);
            String mappedCustomerId = redisTemplate.opsForValue().get(forwardKey(platform, externalUserId));
            if (String.valueOf(customerId).equals(mappedCustomerId)) {
                result.add(readBinding(platform, externalUserId, customerId));
            }
        }
        return result;
    }

    @Override
    public void unbind(Long customerId, String platform, String externalUserId) {
        String normalizedPlatform = normalizePlatform(platform);
        String normalizedExternalId = normalizeExternalId(externalUserId);
        validatePlatform(normalizedPlatform);
        migrateLegacyIdentity(normalizedPlatform, normalizedExternalId);
        migrateLegacyReverseMember(customerId, normalizedPlatform, normalizedExternalId);
        String reverseMember = reverseMember(normalizedPlatform, normalizedExternalId);
        Long result = redisTemplate.execute(UNBIND_SCRIPT,
                List.of(forwardKey(normalizedPlatform, normalizedExternalId), reverseKey(customerId),
                        metaKey(normalizedPlatform, normalizedExternalId), versionKey(normalizedPlatform, normalizedExternalId)),
                String.valueOf(customerId), reverseMember);
        if (Long.valueOf(-1L).equals(result)) {
            throw new AuthException(403, "无权解绑该平台身份");
        }
    }

    @Override
    public void unbindByReference(Long customerId, String platform, String bindingReference) {
        String externalUserId = resolveExternalId(customerId, platform, bindingReference);
        unbind(customerId, platform, externalUserId);
    }

    @Override
    public void moveBinding(Long sourceCustomerId, Long targetCustomerId, String platform, String externalUserId) {
        if (sourceCustomerId == null || targetCustomerId == null || sourceCustomerId.equals(targetCustomerId)) {
            throw new IllegalArgumentException("原商城用户与目标商城用户必须不同");
        }
        User target = userMapper.getById(targetCustomerId);
        if (target == null || target.getStatus() != null && target.getStatus() == 0) {
            throw new IllegalArgumentException("目标商城用户不存在或已被禁用");
        }
        String normalizedPlatform = normalizePlatform(platform);
        String normalizedExternalId = normalizeExternalId(externalUserId);
        validatePlatform(normalizedPlatform);
        migrateLegacyIdentity(normalizedPlatform, normalizedExternalId);
        migrateLegacyReverseMember(sourceCustomerId, normalizedPlatform, normalizedExternalId);
        String reverseMember = reverseMember(normalizedPlatform, normalizedExternalId);
        Long result = redisTemplate.execute(MOVE_SCRIPT,
                List.of(forwardKey(normalizedPlatform, normalizedExternalId), reverseKey(sourceCustomerId),
                        reverseKey(targetCustomerId), metaKey(normalizedPlatform, normalizedExternalId),
                        versionKey(normalizedPlatform, normalizedExternalId)),
                String.valueOf(sourceCustomerId), reverseMember,
                String.valueOf(targetCustomerId), Instant.now().toString());
        if (Long.valueOf(-1L).equals(result)) {
            throw new AuthException(409, "IDENTITY_CONFLICT: 平台身份不属于指定原用户");
        }
        if (Long.valueOf(0L).equals(result)) {
            throw new IllegalArgumentException("平台身份未绑定");
        }
    }

    @Override
    public void moveBindingByReference(Long sourceCustomerId, Long targetCustomerId, String platform,
                                        String bindingReference) {
        String externalUserId = resolveExternalId(sourceCustomerId, platform, bindingReference);
        moveBinding(sourceCustomerId, targetCustomerId, platform, externalUserId);
    }

    @Override
    public PlatformBindingVO resolve(String platform, String externalUserId) {
        String normalizedPlatform = normalizePlatform(platform);
        String normalizedExternalId = normalizeExternalId(externalUserId);
        validatePlatform(normalizedPlatform);
        Long customerId = resolveCustomerId(normalizedPlatform, normalizedExternalId);
        return readBinding(normalizedPlatform, normalizedExternalId, Long.valueOf(customerId));
    }

    @Override
    public String resolveUsername(String platform, String externalUserId) {
        String normalizedPlatform = normalizePlatform(platform);
        String normalizedExternalId = normalizeExternalId(externalUserId);
        validatePlatform(normalizedPlatform);
        User user = userMapper.getById(resolveCustomerId(normalizedPlatform, normalizedExternalId));
        if (user == null || user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalStateException("商城用户不存在或已被禁用");
        }
        return user.getUsername();
    }

    @Override
    public AiResolvedIdentityVO resolveAiIdentity(String platform, String externalUserId) {
        String normalizedPlatform = normalizePlatform(platform);
        String normalizedExternalId = normalizeExternalId(externalUserId);
        validatePlatform(normalizedPlatform);
        Long customerId = resolveCustomerId(normalizedPlatform, normalizedExternalId);
        User user = userMapper.getById(customerId);
        if (user == null || user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalStateException("商城用户不存在或已被禁用");
        }
        AiResolvedIdentityVO result = new AiResolvedIdentityVO();
        result.setPlatform(normalizedPlatform);
        result.setCustomerId(customerId);
        result.setUsername(user.getUsername());
        return result;
    }

    private Long resolveCustomerId(String platform, String externalUserId) {
        migrateLegacyIdentity(platform, externalUserId);
        String customerId = redisTemplate.opsForValue().get(forwardKey(platform, externalUserId));
        if (customerId == null) {
            throw new AuthException(401, "IDENTITY_NOT_BOUND: 当前平台身份尚未绑定商城账号");
        }
        return Long.valueOf(customerId);
    }

    private PlatformBindingVO readBinding(String platform, String externalUserId, Long customerId) {
        User user = userMapper.getById(customerId);
        if (user == null || user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalStateException("商城用户不存在或已被禁用");
        }
        Map<Object, Object> meta = redisTemplate.opsForHash().entries(metaKey(platform, externalUserId));
        PlatformBindingVO result = new PlatformBindingVO();
        result.setPlatform(platform);
        result.setBindingReference(contextSignature.digestPlatformIdentity(platform, externalUserId));
        result.setExternalUserId(externalUserId);
        result.setStatus("active");
        result.setBindingVersion(Long.valueOf(String.valueOf(meta.getOrDefault("version", "1"))));
        Object boundAt = meta.get("boundAt");
        if (boundAt != null) {
            result.setBoundAt(Instant.parse(String.valueOf(boundAt)));
        }
        return result;
    }

    private String resolveExternalId(Long customerId, String platform, String bindingReference) {
        String normalizedPlatform = normalizePlatform(platform);
        String reference = bindingReference == null ? "" : bindingReference.trim();
        validatePlatform(normalizedPlatform);
        if (customerId == null || reference.isEmpty() || reference.length() > 128) {
            throw new IllegalArgumentException("平台绑定引用无效");
        }
        return listBindings(customerId).stream()
                .filter(binding -> normalizedPlatform.equals(binding.getPlatform()))
                .filter(binding -> MessageDigest.isEqual(reference.getBytes(StandardCharsets.UTF_8),
                        binding.getBindingReference().getBytes(StandardCharsets.UTF_8)))
                .map(PlatformBindingVO::getExternalUserId)
                .findFirst()
                .orElseThrow(() -> new AuthException(404, "平台身份未绑定或绑定引用无效"));
    }

    private BindingToken readToken(String token) {
        String raw = redisTemplate.opsForValue().get(TOKEN_PREFIX + contextSignature.digest(token));
        if (raw == null) return null;
        try {
            return objectMapper.readValue(raw, BindingToken.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("绑定凭证格式错误", e);
        }
    }

    private String serializeToken(BindingToken token) {
        try {
            return objectMapper.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("绑定凭证格式错误", e);
        }
    }

    private String versionKey(String platform, String externalUserId) {
        return metaKey(platform, externalUserId) + ":version";
    }

    private void putJson(String key, Object value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), ttl);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("无法保存绑定凭证", e);
        }
    }

    private String randomToken() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String normalizePlatform(String platform) {
        return platform == null ? "" : platform.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeExternalId(String externalUserId) {
        String value = externalUserId == null ? "" : externalUserId.trim();
        if (value.isEmpty() || value.length() > 256) {
            throw new IllegalArgumentException("外部用户 ID 无效");
        }
        return value;
    }

    private void validatePlatform(String platform) {
        if (!"telegram".equals(platform) && !"qq".equals(platform)) {
            throw new IllegalArgumentException("暂不支持该平台");
        }
    }

    private String forwardKey(String platform, String externalUserId) {
        return KEY_PREFIX + "f:" + identityKey(platform, externalUserId);
    }

    private String reverseKey(Long customerId) {
        return KEY_PREFIX + "r:" + customerId;
    }

    private String metaKey(String platform, String externalUserId) {
        return KEY_PREFIX + "meta:" + identityKey(platform, externalUserId);
    }

    private String metaKeyByHash(String identityHash) {
        return KEY_PREFIX + "meta:" + identityHash;
    }

    private String identityKey(String platform, String externalUserId) {
        return contextSignature.digestPlatformIdentity(platform, externalUserId);
    }

    private String reverseMember(String platform, String externalUserId) {
        return platform + ":" + identityKey(platform, externalUserId);
    }

    private void migrateLegacyReverseMember(Long customerId, String platform, String externalUserId) {
        String legacyMember = platform + ":" + externalUserId;
        redisTemplate.execute(MIGRATE_REVERSE_MEMBER_SCRIPT,
                List.of(reverseKey(customerId), forwardKey(platform, externalUserId),
                        metaKey(platform, externalUserId)),
                legacyMember, reverseMember(platform, externalUserId), String.valueOf(customerId),
                contextSignature.encryptPlatformExternalId(platform, externalUserId));
    }

    private void migrateLegacyIdentity(String platform, String externalUserId) {
        String legacyIdentityKey = legacyIdentityKey(platform, externalUserId);
        String currentIdentityKey = identityKey(platform, externalUserId);
        if (legacyIdentityKey.equals(currentIdentityKey)) return;
        Long migrated = redisTemplate.execute(MIGRATE_LEGACY_IDENTITY_SCRIPT,
                List.of(KEY_PREFIX + "f:" + legacyIdentityKey, KEY_PREFIX + "f:" + currentIdentityKey,
                        KEY_PREFIX + "meta:" + legacyIdentityKey, KEY_PREFIX + "meta:" + currentIdentityKey,
                        KEY_PREFIX + "meta:" + legacyIdentityKey + ":version",
                        KEY_PREFIX + "meta:" + currentIdentityKey + ":version"),
                reverseMember(platform, externalUserId), KEY_PREFIX + "r:", platform + ":" + externalUserId,
                contextSignature.encryptPlatformExternalId(platform, externalUserId));
        if (Long.valueOf(-1L).equals(migrated)) {
            throw new AuthException(409, "IDENTITY_CONFLICT: legacy and current identity mappings disagree");
        }
    }

    private String legacyIdentityKey(String platform, String externalUserId) {
        try {
            byte[] value = (platform + "\u0000" + externalUserId).getBytes(StandardCharsets.UTF_8);
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(MessageDigest.getInstance("SHA-256").digest(value));
        } catch (Exception ex) {
            throw new IllegalStateException("无法生成旧版身份键", ex);
        }
    }

    private record BindingToken(Long customerId, String platform, Instant expiresAt) {
    }
}
