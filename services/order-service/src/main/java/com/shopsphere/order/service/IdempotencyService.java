package com.shopsphere.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:checkout:";
    private static final Duration KEY_EXPIRATION = Duration.ofHours(24);

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean tryAcquire(String idempotencyKey, UUID userId) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return true; // No idempotency key provided, allow request
        }

        String key = IDEMPOTENCY_KEY_PREFIX + userId + ":" + idempotencyKey;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "processing", KEY_EXPIRATION);

        if (Boolean.TRUE.equals(success)) {
            log.debug("Acquired idempotency lock for key: {}", idempotencyKey);
            return true;
        }

        log.warn("Duplicate checkout request detected with idempotency key: {}", idempotencyKey);
        return false;
    }

    public void markCompleted(String idempotencyKey, UUID userId, UUID orderId) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        String key = IDEMPOTENCY_KEY_PREFIX + userId + ":" + idempotencyKey;
        redisTemplate.opsForValue().set(key, orderId.toString(), KEY_EXPIRATION);
        log.debug("Marked idempotency key {} as completed with order {}", idempotencyKey, orderId);
    }

    public UUID getExistingOrderId(String idempotencyKey, UUID userId) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return null;
        }

        String key = IDEMPOTENCY_KEY_PREFIX + userId + ":" + idempotencyKey;
        Object value = redisTemplate.opsForValue().get(key);

        if (value != null && !"processing".equals(value.toString())) {
            try {
                return UUID.fromString(value.toString());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid order ID stored for idempotency key: {}", idempotencyKey);
            }
        }
        return null;
    }

    public void release(String idempotencyKey, UUID userId) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        String key = IDEMPOTENCY_KEY_PREFIX + userId + ":" + idempotencyKey;
        redisTemplate.delete(key);
        log.debug("Released idempotency lock for key: {}", idempotencyKey);
    }
}
