package com.fsb.auth;

import com.fsb.exception.AuthException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AiRequestReplayGuard {
    private final StringRedisTemplate redisTemplate;

    public AiRequestReplayGuard(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void consume(String nonce) {
        if (nonce == null || nonce.isBlank() || nonce.length() > 128) {
            throw new AuthException(401, "INVALID_CONTEXT: invalid AI request nonce");
        }
        final Boolean firstUse;
        try {
            firstUse = redisTemplate.opsForValue().setIfAbsent(
                    "cs:ai:nonce:" + nonce, "1", Duration.ofMinutes(2));
        } catch (DataAccessException ex) {
            throw new AuthException(503, "UPSTREAM_ERROR: AI replay protection store unavailable");
        }
        if (!Boolean.TRUE.equals(firstUse)) {
            throw new AuthException(401, "REPLAY_DETECTED: AI request nonce already used");
        }
    }
}
