package com.fsb.auth;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnabledIfEnvironmentVariable(named = "BAT_IDENTITY_REDIS_IT", matches = "true")
class AiConcurrencyLimiterRedisIntegrationTest {
    private static LettuceConnectionFactory connectionFactory;
    private static StringRedisTemplate redis;
    private static String testKey;

    @BeforeAll
    static void connect() {
        String host = System.getenv().getOrDefault("REDIS_HOST", "127.0.0.1");
        int port = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));
        connectionFactory = new LettuceConnectionFactory(host, port);
        connectionFactory.afterPropertiesSet();
        redis = new StringRedisTemplate(connectionFactory);
        redis.afterPropertiesSet();
        testKey = "cs:test:ai:concurrency:" + UUID.randomUUID();
        redis.delete(testKey);
    }

    @AfterAll
    static void close() {
        if (redis != null) {
            redis.delete(testKey);
        }
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    @Test
    void independentLimiterInstancesShareOneAtomicCapacity() throws Exception {
        AiConcurrencyLimiter firstInstance = new AiConcurrencyLimiter(redis, 2, 30, testKey);
        AiConcurrencyLimiter secondInstance = new AiConcurrencyLimiter(redis, 2, 30, testKey);
        int workersCount = 12;
        CountDownLatch ready = new CountDownLatch(workersCount);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService workers = Executors.newFixedThreadPool(workersCount);
        List<Future<String>> attempts = new ArrayList<>();

        try {
            for (int index = 0; index < workersCount; index++) {
                AiConcurrencyLimiter limiter = index % 2 == 0 ? firstInstance : secondInstance;
                attempts.add(workers.submit(() -> {
                    ready.countDown();
                    start.await();
                    return limiter.tryAcquire();
                }));
            }
            ready.await();
            start.countDown();

            List<String> leases = new ArrayList<>();
            for (Future<String> attempt : attempts) {
                String lease = attempt.get();
                if (lease != null) {
                    leases.add(lease);
                }
            }
            assertEquals(2, leases.size());
            leases.forEach(firstInstance::release);
            String releasedSlot = secondInstance.tryAcquire();
            assertEquals(1, releasedSlot == null ? 0 : 1);
            secondInstance.release(releasedSlot);
        } finally {
            workers.shutdownNow();
        }
    }

    @Test
    void activeLeaseRenewsPastItsInitialExpiryAndRepeatedReleaseIsSafe() throws Exception {
        AiConcurrencyLimiter limiter = new AiConcurrencyLimiter(redis, 1, 1, testKey);
        String lease = limiter.tryAcquire();
        assertNotNull(lease);

        Thread.sleep(1500);

        assertNull(limiter.tryAcquire());
        limiter.release(lease);
        limiter.release(lease);
        String replacement = limiter.tryAcquire();
        assertNotNull(replacement);
        limiter.release(replacement);
    }
}
