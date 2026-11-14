package com.fsb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsb.Mapper.UserMapper;
import com.fsb.Service.impl.PlatformIdentityServiceImpl;
import com.fsb.auth.AiContextSignature;
import com.fsb.exception.AuthException;
import com.fsb.pojo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PlatformIdentityServiceImplTest {
    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Test
    void rejectsMovingIdentityToSameCustomerBeforeRedisAccess() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        UserMapper users = mock(UserMapper.class);
        PlatformIdentityServiceImpl service = service(redis, users);

        assertThrows(IllegalArgumentException.class,
                () -> service.moveBinding(10L, 10L, "telegram", "external-1"));
        verifyNoInteractions(redis);
    }

    @Test
    void rejectsMovingIdentityToMissingOrDisabledCustomer() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        UserMapper users = mock(UserMapper.class);
        when(users.getById(20L)).thenReturn(null);
        PlatformIdentityServiceImpl service = service(redis, users);

        assertThrows(IllegalArgumentException.class,
                () -> service.moveBinding(10L, 20L, "telegram", "external-1"));
        verifyNoInteractions(redis);

        User disabled = new User();
        disabled.setId(20L);
        disabled.setStatus(0);
        when(users.getById(20L)).thenReturn(disabled);
        assertThrows(IllegalArgumentException.class,
                () -> service.moveBinding(10L, 20L, "telegram", "external-1"));
        verifyNoInteractions(redis);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void movesIdentityWithAtomicRedisScriptsWithoutExpiringLocks() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> values = mock(ValueOperations.class);
        UserMapper users = mock(UserMapper.class);
        User target = new User();
        target.setId(20L);
        target.setStatus(1);
        when(users.getById(20L)).thenReturn(target);
        when(redis.execute(any(RedisScript.class), anyList(), any(Object[].class))).thenReturn(1L);
        PlatformIdentityServiceImpl service = service(redis, users);

        assertDoesNotThrow(() -> service.moveBinding(10L, 20L, " Telegram ", "external-1"));

        // Legacy-index compatibility runs two atomic migrations before the
        // final move script; none of these paths may use a lock or read-then-write.
        verify(redis, times(3)).execute(any(RedisScript.class), anyList(), any(Object[].class));
        verify(redis, never()).opsForValue();
    }

    @Test
    void rejectsUnsupportedPlatformBeforeTakingAnyLock() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        UserMapper users = mock(UserMapper.class);
        User target = new User();
        target.setId(20L);
        target.setStatus(1);
        when(users.getById(20L)).thenReturn(target);
        PlatformIdentityServiceImpl service = service(redis, users);

        assertThrows(IllegalArgumentException.class,
                () -> service.moveBinding(10L, 20L, "discord", "external-1"));
        verify(redis, never()).opsForValue();
    }

    private PlatformIdentityServiceImpl service(StringRedisTemplate redis, UserMapper users) {
        return new PlatformIdentityServiceImpl(redis, new ObjectMapper(), users, new AiContextSignature(SECRET));
    }

    private String identityHash(String platform, String externalUserId) {
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(
                digest((platform + "\u0000" + externalUserId).getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    }

    private byte[] digest(byte[] value) {
        try {
            return java.security.MessageDigest.getInstance("SHA-256").digest(value);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
