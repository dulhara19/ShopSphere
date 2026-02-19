package com.shopsphere.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recommendation_events")
public class RecommendationEvent {

    @Id
    private String id;

    private String eventType;
    private String productId;
    private String userId;      // optional
    private String sessionId;   // for anonymous users
    private Map<String, Object> metadata;
    private Instant timestamp;

    // ===============================
    // Helper method for PRODUCT_VIEW
    // ===============================
    public static RecommendationEvent productView(String productId, String userId, String sessionId, Map<String, Object> additionalMetadata) {
        Map<String, Object> metadata = additionalMetadata != null ? new HashMap<>(additionalMetadata) : new HashMap<>();

        return RecommendationEvent.builder()
            .eventType("PRODUCT_VIEW")
            .productId(productId)
            .userId(userId)
            .sessionId(sessionId)
            .metadata(metadata)
            .timestamp(Instant.now())
            .build();
    }

    // ===============================
    // Helper method for SEARCH_QUERY
    // ===============================
    public static RecommendationEvent searchQuery(String userId, String sessionId, String searchTerm, Map<String, Object> additionalMetadata) {
        Map<String, Object> metadata = additionalMetadata != null ? new HashMap<>(additionalMetadata) : new HashMap<>();
        metadata.put("searchTerm", searchTerm);  // now properly stored

        return RecommendationEvent.builder()
            .eventType("SEARCH_QUERY")
            .userId(userId)
            .sessionId(sessionId)
            .metadata(metadata)
            .timestamp(Instant.now())
            .build();
    }

    // ===============================
    // Helper method for ADD_TO_CART
    // ===============================
    public static RecommendationEvent addToCart(String productId, String userId, String sessionId, Integer quantity, Map<String, Object> additionalMetadata) {
        Map<String, Object> metadata = additionalMetadata != null ? new HashMap<>(additionalMetadata) : new HashMap<>();
        metadata.put("quantity", quantity);  // now properly stored

        return RecommendationEvent.builder()
            .eventType("ADD_TO_CART")
            .productId(productId)
            .userId(userId)
            .sessionId(sessionId)
            .metadata(metadata)
            .timestamp(Instant.now())
            .build();
    }

    // ===============================
// Helper method for PURCHASE
// ===============================
    public static RecommendationEvent purchase(String orderId, String userId, String sessionId, Map<String, Object> additionalMetadata) {
        Map<String, Object> metadata = additionalMetadata != null ? new HashMap<>(additionalMetadata) : new HashMap<>();
        metadata.put("orderId", orderId);

        return RecommendationEvent.builder()
            .eventType("PURCHASE")
            .userId(userId)
            .sessionId(sessionId)
            .metadata(metadata)
            .timestamp(Instant.now())
            .build();
    }

}
