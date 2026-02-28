package com.shopsphere.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * TrendingProduct - Stores trending products with scores
 * Calculated based on views, cart adds, and purchases
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trending_products")
public class TrendingProduct {

    @Id
    private String id;

    private String productId;
    private String categoryId;          // for category; null for global
    private Double trendingScore;       // calculated score
    private long viewCount;
    private long cartAddCount;
    private long purchaseCount;
    private Instant lastUpdated;
    private Integer rank;               // ranking position

    /**
     * Calculate trending score
     * Formula: (views * 1) + (cart_adds * 3) + (purchases * 5) × time_decay_factor
     */
    public static Double calculateTrendingScore(long views, long cartAdds, long purchases, Instant timestamp) {
        double baseScore = (views * 1.0) + (cartAdds * 3.0) + (purchases * 5.0);

        // Apply time decay: reduce score for older timestamps
        long hoursSinceEvent = java.time.temporal.ChronoUnit.HOURS.between(timestamp, Instant.now());
        double timeDecayFactor = Math.exp(-0.1 * hoursSinceEvent);

        return baseScore * timeDecayFactor;
    }
}
