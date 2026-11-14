package com.fsb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsb.Mapper.UserMapper;
import com.fsb.Service.impl.PlatformIdentityServiceImpl;
import com.fsb.auth.AiContextSignature;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.PlatformBindingConfirmRequest;
import com.fsb.pojo.VO.PlatformBindingTokenVO;
import com.fsb.pojo.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "BAT_IDENTITY_REDIS_IT", matches = "true")
class PlatformIdentityRedisIntegrationTest {
    private static final String SECRET = "0123456789abcdef0123456789abcdef";
    private static LettuceConnectionFactory connectionFactory;
    private static StringRedisTemplate redis;
    private static PlatformIdentityServiceImpl service;
    private static Long sourceId;
    private static Long targetId;
    private static String externalId;

    @BeforeAll
    static void connect() {
        String host = System.getenv().getOrDefault("REDIS_HOST", "127.0.0.1");
        int port = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));
        sourceId = Math.abs(UUID.randomUUID().getMostSignificantBits());
        targetId = sourceId + 1;
        externalId = "integration-" + UUID.randomUUID();
        connectionFactory = new LettuceConnectionFactory(host, port);
        connectionFactory.afterPropertiesSet();
        redis = new StringRedisTemplate(connectionFactory);
        redis.afterPropertiesSet();
        User source = new User();
        source.setId(sourceId);
        source.setUsername("it-source");
        source.setStatus(1);
        User target = new User();
        target.setId(targetId);
        target.setUsername("it-target");
        target.setStatus(1);
        UserMapper users = (UserMapper) Proxy.newProxyInstance(UserMapper.class.getClassLoader(),
                new Class<?>[]{UserMapper.class}, (proxy, method, args) -> {
                    if ("getById".equals(method.getName())) {
                        return sourceId.equals(args[0]) ? source : targetId.equals(args[0]) ? target : null;
                    }
                    throw new UnsupportedOperationException("Unexpected mapper method: " + method.getName());
                });
        service = new PlatformIdentityServiceImpl(redis, new ObjectMapper().findAndRegisterModules(), users,
                new AiContextSignature(SECRET));
    }

    @AfterAll
    static void close() {
        if (connectionFactory != null) connectionFactory.destroy();
    }

    @BeforeEach
    void clearReverseIndexes() {
        redis.delete("cs:identity:r:" + sourceId);
        redis.delete("cs:identity:r:" + targetId);
    }

    @Test
    void bindRetryMoveAndUnbindStayConsistentAcrossRedisIndexes() {
        PlatformBindingTokenVO binding = service.startBinding(sourceId, "telegram");
        PlatformBindingConfirmRequest request = confirm(binding.getToken(), "retry-1");

        service.confirmBinding(request);
        service.confirmBinding(request);
        assertEquals("it-source", service.resolveUsername("telegram", externalId));
        assertEquals(1, service.listBindings(sourceId).size());
        var reverseMembers = redis.opsForSet().members("cs:identity:r:" + sourceId);
        assertEquals(1, reverseMembers.size());
        org.junit.jupiter.api.Assertions.assertFalse(reverseMembers.iterator().next().contains(externalId));
        assertTrue(reverseMembers.iterator().next().startsWith("telegram:" + serviceHash("telegram", externalId)));

        PlatformBindingTokenVO duplicateToken = service.startBinding(sourceId, "telegram");
        service.confirmBinding(confirm(duplicateToken.getToken(), "retry-new-command"));
        assertEquals(1, service.listBindings(sourceId).size());

        service.moveBinding(sourceId, targetId, "telegram", externalId);
        assertEquals(0, service.listBindings(sourceId).size());
        var targetBindings = service.listBindings(targetId);
        assertEquals(1, targetBindings.size());
        assertTrue(targetBindings.get(0).getBindingReference().matches("[A-Za-z0-9_-]{40,}"));
        assertEquals("it-target", service.resolveUsername("telegram", externalId));

        assertThrows(AuthException.class,
                () -> service.unbindByReference(targetId, "telegram", "incorrect-binding-reference"));
        assertEquals(1, service.listBindings(targetId).size());

        service.unbindByReference(targetId, "telegram", targetBindings.get(0).getBindingReference());
        assertEquals(0, service.listBindings(targetId).size());
        assertThrows(AuthException.class, () -> service.resolveUsername("telegram", externalId));
    }

    @Test
    void concurrentBindingsForOneExternalIdentityHaveExactlyOneWinner() throws Exception {
        String contestedExternalId = "concurrent-" + UUID.randomUUID();
        PlatformBindingTokenVO sourceToken = service.startBinding(sourceId, "telegram");
        PlatformBindingTokenVO targetToken = service.startBinding(targetId, "telegram");
        PlatformBindingConfirmRequest sourceRequest = confirm(sourceToken.getToken(), "race-source");
        PlatformBindingConfirmRequest targetRequest = confirm(targetToken.getToken(), "race-target");
        sourceRequest.setExternalUserId(contestedExternalId);
        targetRequest.setExternalUserId(contestedExternalId);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService workers = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> sourceResult = workers.submit(() -> bindAfter(start, ready, sourceRequest));
            Future<Boolean> targetResult = workers.submit(() -> bindAfter(start, ready, targetRequest));
            ready.await();
            start.countDown();
            assertEquals(1, (sourceResult.get() ? 1 : 0) + (targetResult.get() ? 1 : 0));
            String owner = service.resolveUsername("telegram", contestedExternalId);
            Long ownerId = "it-source".equals(owner) ? sourceId : targetId;
            service.unbind(ownerId, "telegram", contestedExternalId);
        } finally {
            workers.shutdownNow();
        }
    }

    @Test
    void staleReverseIndexIsIgnored() {
        redis.opsForSet().add("cs:identity:r:" + sourceId, "telegram:stale-" + UUID.randomUUID());

        assertEquals(0, service.listBindings(sourceId).size());
    }

    @Test
    void legacyShaIdentityIndexIsMigratedOnFirstLookup() {
        String legacyExternalId = "legacy-" + UUID.randomUUID();
        String legacyHash = legacyHash("telegram", legacyExternalId);
        redis.opsForValue().set("cs:identity:f:" + legacyHash, String.valueOf(sourceId));
        redis.opsForHash().put("cs:identity:meta:" + legacyHash, "customerId", String.valueOf(sourceId));
        redis.opsForHash().put("cs:identity:meta:" + legacyHash, "version", "7");
        redis.opsForHash().put("cs:identity:meta:" + legacyHash, "boundAt", java.time.Instant.now().toString());
        redis.opsForHash().put("cs:identity:meta:" + legacyHash, "status", "active");
        redis.opsForValue().set("cs:identity:meta:" + legacyHash + ":version", "7");

        assertEquals("it-source", service.resolveUsername("telegram", legacyExternalId));
        var migratedBinding = service.listBindings(sourceId).stream()
                .filter(binding -> "telegram".equals(binding.getPlatform())
                        && legacyExternalId.equals(binding.getExternalUserId()))
                .findFirst().orElseThrow();
        assertEquals(7L, migratedBinding.getBindingVersion());
        assertEquals(0L, redis.hasKey("cs:identity:f:" + legacyHash) ? 1L : 0L);
        var reverseMembers = redis.opsForSet().members("cs:identity:r:" + sourceId);
        assertTrue(reverseMembers.stream().noneMatch(member -> member.contains(legacyExternalId)));
    }

    @Test
    void legacyPlainReverseMemberIsMigratedWhenForwardIndexIsAlreadyHmacKeyed() {
        String legacyExternalId = "plain-reverse-" + UUID.randomUUID();
        String currentHash = Base64.getUrlEncoder().withoutPadding().encodeToString(
                hmac("platform-identity\ntelegram\n" + legacyExternalId));
        redis.opsForValue().set("cs:identity:f:" + currentHash, String.valueOf(sourceId));
        redis.opsForHash().put("cs:identity:meta:" + currentHash, "version", "3");
        redis.opsForHash().put("cs:identity:meta:" + currentHash, "boundAt", java.time.Instant.now().toString());
        redis.opsForSet().add("cs:identity:r:" + sourceId, "telegram:" + legacyExternalId);

        var binding = service.listBindings(sourceId).stream()
                .filter(item -> legacyExternalId.equals(item.getExternalUserId()))
                .findFirst().orElseThrow();

        assertEquals("telegram", binding.getPlatform());
        var members = redis.opsForSet().members("cs:identity:r:" + sourceId);
        assertEquals(1, members.size());
        assertTrue(members.iterator().next().startsWith("telegram:" + currentHash));
        assertTrue(members.stream().noneMatch(member -> member.contains(legacyExternalId)));
    }

    @Test
    void conflictingLegacyAndCurrentIdentityMappingsAreRejected() {
        String conflictExternalId = "legacy-conflict-" + UUID.randomUUID();
        String legacyHash = legacyHash("telegram", conflictExternalId);
        String currentHash = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(
                hmac("platform-identity\ntelegram\n" + conflictExternalId));
        redis.opsForValue().set("cs:identity:f:" + legacyHash, String.valueOf(sourceId));
        redis.opsForValue().set("cs:identity:f:" + currentHash, String.valueOf(targetId));

        assertThrows(AuthException.class, () -> service.resolveUsername("telegram", conflictExternalId));
    }

    private boolean bindAfter(CountDownLatch start, CountDownLatch ready,
                              PlatformBindingConfirmRequest request) throws InterruptedException {
        ready.countDown();
        start.await();
        try {
            service.confirmBinding(request);
            return true;
        } catch (AuthException conflict) {
            return false;
        }
    }

    private PlatformBindingConfirmRequest confirm(String token, String idempotencyKey) {
        PlatformBindingConfirmRequest request = new PlatformBindingConfirmRequest();
        request.setPlatform("telegram");
        request.setExternalUserId(externalId);
        request.setToken(token);
        request.setIdempotencyKey(idempotencyKey);
        return request;
    }

    private String legacyHash(String platform, String externalId) {
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(MessageDigest.getInstance("SHA-256")
                    .digest((platform + "\u0000" + externalId).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private String serviceHash(String platform, String externalId) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(
                hmac("platform-identity\n" + platform + "\n" + externalId));
    }

    private byte[] hmac(String value) {
        try {
            var mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
