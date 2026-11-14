package com.fsb.auth;

import com.fsb.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class AiConcurrencyLimiter {
    private static final DefaultRedisScript<Long> ACQUIRE_SCRIPT = new DefaultRedisScript<>(
            "local time = redis.call('time'); "
                    + "local now = tonumber(time[1]) * 1000 + math.floor(tonumber(time[2]) / 1000); "
                    + "redis.call('zremrangebyscore', KEYS[1], '-inf', now); "
                    + "if redis.call('zcard', KEYS[1]) >= tonumber(ARGV[1]) then return 0 end; "
                    + "redis.call('zadd', KEYS[1], now + tonumber(ARGV[2]), ARGV[3]); "
                    + "redis.call('pexpire', KEYS[1], ARGV[2]); return 1",
            Long.class);
    private static final DefaultRedisScript<Long> RENEW_SCRIPT = new DefaultRedisScript<>(
            "if not redis.call('zscore', KEYS[1], ARGV[1]) then return 0 end; "
                    + "local time = redis.call('time'); "
                    + "local now = tonumber(time[1]) * 1000 + math.floor(tonumber(time[2]) / 1000); "
                    + "redis.call('zadd', KEYS[1], 'XX', now + tonumber(ARGV[2]), ARGV[1]); "
                    + "redis.call('pexpire', KEYS[1], ARGV[2]); return 1",
            Long.class);
    private static final DefaultRedisScript<Long> RELEASE_SCRIPT = new DefaultRedisScript<>(
            "return redis.call('zrem', KEYS[1], ARGV[1])",
            Long.class);
    private static final ScheduledExecutorService RENEWAL_EXECUTOR = Executors.newScheduledThreadPool(1, runnable -> {
        Thread thread = new Thread(runnable, "ai-concurrency-lease-renewal");
        thread.setDaemon(true);
        return thread;
    });

    private final StringRedisTemplate redisTemplate;
    private final String key;
    private final int maxConcurrent;
    private final long leaseMillis;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> renewals = new ConcurrentHashMap<>();

    public AiConcurrencyLimiter(StringRedisTemplate redisTemplate,
                                @Value("${ai.max-concurrent-requests:64}") int maxConcurrent,
                                @Value("${ai.concurrency-lease-seconds:60}") long leaseSeconds,
                                @Value("${ai.concurrency-key:cs:ai:concurrency:leases}") String key) {
        if (maxConcurrent < 1 || leaseSeconds < 1) {
            throw new IllegalArgumentException("AI concurrency limits must be positive");
        }
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("AI concurrency key must not be blank");
        }
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.maxConcurrent = maxConcurrent;
        this.leaseMillis = Duration.ofSeconds(leaseSeconds).toMillis();
    }

    public String tryAcquire() {
        String leaseId = UUID.randomUUID().toString();
        final Long acquired;
        try {
            acquired = redisTemplate.execute(ACQUIRE_SCRIPT, List.of(key),
                    String.valueOf(maxConcurrent), String.valueOf(leaseMillis), leaseId);
        } catch (DataAccessException ex) {
            throw new AuthException(503, "UPSTREAM_ERROR: AI concurrency store unavailable");
        }
        if (!Long.valueOf(1L).equals(acquired)) {
            return null;
        }
        long renewalPeriod = Math.max(1, leaseMillis / 3);
        ScheduledFuture<?> renewal = RENEWAL_EXECUTOR.scheduleAtFixedRate(
                () -> renew(leaseId), renewalPeriod, renewalPeriod, TimeUnit.MILLISECONDS);
        renewals.put(leaseId, renewal);
        return leaseId;
    }

    public void release(String leaseId) {
        if (leaseId != null && !leaseId.isBlank()) {
            ScheduledFuture<?> renewal = renewals.remove(leaseId);
            if (renewal != null) {
                renewal.cancel(false);
            }
            try {
                redisTemplate.execute(RELEASE_SCRIPT, List.of(key), leaseId);
            } catch (DataAccessException ignored) {
                // The lease has a finite TTL; a Redis outage must not mask the response.
            }
        }
    }

    private void renew(String leaseId) {
        try {
            Long renewed = redisTemplate.execute(RENEW_SCRIPT, List.of(key), leaseId, String.valueOf(leaseMillis));
            if (Long.valueOf(0L).equals(renewed)) {
                ScheduledFuture<?> renewal = renewals.remove(leaseId);
                if (renewal != null) {
                    renewal.cancel(false);
                }
            }
        } catch (DataAccessException ignored) {
            // Retry on the next interval; new AI requests still fail closed while Redis is unavailable.
        }
    }
}
