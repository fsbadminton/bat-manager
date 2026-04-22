package com.fsb.Service.impl;

import com.fsb.Service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;
import java.util.regex.Pattern;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MAX_DAILY_SENDS = 5;
    private static final long CODE_TTL_SECONDS = 300; // 5分钟

    private final StringRedisTemplate redisTemplate;

    public EmailServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void sendCode(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("邮箱格式不正确");
        }

        String countKey = "email:count:" + email + ":" + LocalDate.now();
        String countStr = redisTemplate.opsForValue().get(countKey);
        int count = countStr == null ? 0 : Integer.parseInt(countStr);
        if (count >= MAX_DAILY_SENDS) {
            throw new RuntimeException("今日发送次数已达上限，请明天再试");
        }

        String code = String.format("%06d", new Random().nextInt(1000000));

        redisTemplate.opsForValue().set("email:code:" + email, code, Duration.ofSeconds(CODE_TTL_SECONDS));

        // 计数器，TTL 到当天结束
        long secondsUntilMidnight = Duration.between(LocalDateTime.now(),
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)).getSeconds();
        redisTemplate.opsForValue().set(countKey, String.valueOf(count + 1), Duration.ofSeconds(secondsUntilMidnight));

        log.info("【注册验证码】邮箱: {} 验证码: {} (有效期5分钟)", email, code);
    }
}
