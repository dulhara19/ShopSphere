package com.shopsphere.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * TokenBlacklistService - Manages blacklisted JWT tokens in Redis.
 *
 * When a user logs out, their token is added to the blacklist with a TTL
 * matching the token's remaining expiration time.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_KEY_PREFIX = "token:blacklist:";

    /**
     * Add a token to the blacklist with TTL
     *
     * Debug Version: Logs Redis operations and connection details
     *
     * @param token JWT token to blacklist
     * @param expirationTimeMillis Remaining TTL in milliseconds
     */
    public void blacklistToken(String token, long expirationTimeMillis) {
        log.info("========== REDIS BLACKLIST DEBUG START ==========");
        log.info("Token (first 50 chars): {}", token.substring(0, Math.min(50, token.length())));
        log.info("Expiration Time (ms):   {}", expirationTimeMillis);
        log.info("Expiration Time (sec):  {}", expirationTimeMillis / 1000);

        String key = BLACKLIST_KEY_PREFIX + token;
        log.info("Redis Key (first 100 chars): {}", key.substring(0, Math.min(100, key.length())));
        log.info("Redis Value: 'blacklisted'");
        log.info("Redis TTL: {} MILLISECONDS", expirationTimeMillis);

        try {
            log.info("Attempting to connect to Redis...");

            // Check if RedisTemplate is properly configured
            if (redisTemplate == null) {
                log.error("❌ RedisTemplate is NULL! Bean not properly injected.");
                throw new RuntimeException("RedisTemplate not configured");
            }
            log.info("✓ RedisTemplate is available");

            // Try to get connection
            log.info("Attempting SET operation on Redis...");

            // Store token with TTL matching token expiration
            redisTemplate.opsForValue().set(
                key,
                "blacklisted",
                expirationTimeMillis,
                TimeUnit.MILLISECONDS
            );

            log.info("✓ SET operation completed successfully");

            // Verify it was actually stored
            Boolean exists = redisTemplate.hasKey(key);
            log.info("✓ Verification - Token exists in Redis: {}", exists);

            if (!exists) {
                log.warn("⚠ WARNING: Token was not found in Redis after SET operation");
            }

            Long ttl = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
            log.info("✓ Verification - Token TTL in Redis: {} ms ({} seconds)", ttl, ttl != null ? ttl / 1000 : "N/A");

            log.info("✓ Token blacklisted successfully with TTL: {} ms", expirationTimeMillis);
            log.info("========== REDIS BLACKLIST DEBUG END (SUCCESS) ==========");

        } catch (Exception e) {
            log.error("❌ Failed to blacklist token in Redis: {}", e.getMessage());
            log.error("Exception Type: {}", e.getClass().getName());
            log.error("Exception Stack Trace: ", e);
            log.info("========== REDIS BLACKLIST DEBUG END (FAILED) ==========");
            throw new RuntimeException("Failed to blacklist token", e);
        }
    }

    /**
     * Check if a token is blacklisted
     *
     * Debug Version: Logs Redis lookup details
     *
     * @param token JWT token to check
     * @return true if token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_KEY_PREFIX + token;
        log.debug("Checking if token is blacklisted. Key (first 100 chars): {}", key.substring(0, Math.min(100, key.length())));

        try {
            Boolean exists = redisTemplate.hasKey(key);
            boolean isBlacklisted = Boolean.TRUE.equals(exists);

            if (isBlacklisted) {
                log.warn("⚠ Token is blacklisted! Blocking request.");
                Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                log.warn("  Blacklist TTL: {} seconds", ttl);
            } else {
                log.debug("✓ Token is not blacklisted");
            }

            return isBlacklisted;
        } catch (Exception e) {
            log.error("Failed to check token blacklist status in Redis: {}", e.getMessage(), e);
            // Fail open: if Redis is down, allow the request (token validation will still occur)
            log.warn("⚠ Redis check failed - allowing request (fail-open strategy). Token validation will still occur.");
            return false;
        }
    }

    /**
     * Remove a token from the blacklist (optional, mainly for testing)
     *
     * @param token JWT token to remove from blacklist
     */
    public void removeFromBlacklist(String token) {
        String key = BLACKLIST_KEY_PREFIX + token;

        try {
            redisTemplate.delete(key);
            log.info("Token removed from blacklist");
        } catch (Exception e) {
            log.error("Failed to remove token from blacklist: {}", e.getMessage(), e);
        }
    }
}

