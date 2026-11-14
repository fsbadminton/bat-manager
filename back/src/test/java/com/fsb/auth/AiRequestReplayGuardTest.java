package com.fsb.auth;

import com.fsb.exception.AuthException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiRequestReplayGuardTest {
    @Test
    @SuppressWarnings("unchecked")
    void consumesNonceOnceAndRejectsReplay() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        when(values.setIfAbsent("cs:ai:nonce:nonce-1", "1", Duration.ofMinutes(2)))
                .thenReturn(true, false);
        AiRequestReplayGuard guard = new AiRequestReplayGuard(redis);

        guard.consume("nonce-1");
        AuthException replay = assertThrows(AuthException.class, () -> guard.consume("nonce-1"));
        assertEquals(401, replay.getCode());
    }

    @Test
    void rejectsEmptyNonce() {
        AiRequestReplayGuard guard = new AiRequestReplayGuard(mock(StringRedisTemplate.class));
        AuthException invalid = assertThrows(AuthException.class, () -> guard.consume(" "));
        assertEquals(401, invalid.getCode());
    }

    @Test
    void failsClosedWhenReplayStoreIsUnavailable() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        when(values.setIfAbsent("cs:ai:nonce:nonce-1", "1", Duration.ofMinutes(2)))
                .thenThrow(new DataAccessResourceFailureException("redis unavailable"));
        AiRequestReplayGuard guard = new AiRequestReplayGuard(redis);

        AuthException unavailable = assertThrows(AuthException.class, () -> guard.consume("nonce-1"));
        assertEquals(503, unavailable.getCode());
    }
}
