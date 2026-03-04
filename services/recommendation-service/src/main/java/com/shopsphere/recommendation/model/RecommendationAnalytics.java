package com.shopsphere.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Analytics events for recommendations (clicks, conversions etc.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recommendation_analytics")
public class RecommendationAnalytics {
    @Id
    private String id;

    private String userId;
    private String recommendationId; // could be productId or other context
    private String type; // "click", "conversion", "view"
    private Instant timestamp;
    private String metadata;
}
