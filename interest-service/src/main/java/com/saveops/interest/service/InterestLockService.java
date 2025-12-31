package com.saveops.interest.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;

@Component
public class InterestLockService {
    private static final String LOCK_KEY = "interest:daily-accrual-lock";
    private final StringRedisTemplate redisTemplate;

    public InterestLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryAcquireDailyLock(LocalDate date) {
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY + ":" + date, "locked", Duration.ofHours(6));
        return Boolean.TRUE.equals(locked);
    }
}

