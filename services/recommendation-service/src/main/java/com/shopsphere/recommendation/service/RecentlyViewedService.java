package com.shopsphere.recommendation.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecentlyViewedService {

    private final RedisTemplate<String, String> redisTemplate; // Use String for keys and values

    public RecentlyViewedService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Generate Redis key for a user
    private String getKey(String userId) {
        return "recently-viewed:" + userId;
    }

    /**
     * Add a product to the recently viewed list for a user
     */
    public void addRecentlyViewed(String userId, String productId) {
        String key = getKey(userId);

        // Remove product if already exists (avoid duplicates)
        redisTemplate.opsForList().remove(key, 1, productId);

        // Add to beginning of list
        redisTemplate.opsForList().leftPush(key, productId);

        // Keep only the latest 10 products
        redisTemplate.opsForList().trim(key, 0, 9);

        // Auto-expire the list after 30 days
        redisTemplate.expire(key, Duration.ofDays(30));
    }

    /**
     * Get the last N recently viewed products for a user
     */
    public List<String> getRecentlyViewed(String userId, int limit) {
        String key = getKey(userId);

        List<String> items = redisTemplate.opsForList().range(key, 0, limit - 1);

        // Handle null case
        if (items == null) {
            return List.of();
        }

        return items;
    }

    /**
     * Clear recently viewed history for a user
     */
    public void clearRecentlyViewed(String userId) {
        String key = getKey(userId);
        redisTemplate.delete(key);
    }

    /**
     * Merge session-based history into a logged-in user's history
     */
    public void mergeSessionToUser(String userId, String sessionId) {
        String sessionKey = "recently-viewed:" + sessionId;
        String userKey = getKey(userId);

        List<String> sessionProducts = redisTemplate.opsForList().range(sessionKey, 0, -1)
            .stream().map(Object::toString).collect(Collectors.toList());

        if (sessionProducts != null && !sessionProducts.isEmpty()) {
            // Add each product to user's list
            for (String productId : sessionProducts) {
                addRecentlyViewed(userId, productId);
            }

            // Delete session key
            redisTemplate.delete(sessionKey);
        }
    }
}
