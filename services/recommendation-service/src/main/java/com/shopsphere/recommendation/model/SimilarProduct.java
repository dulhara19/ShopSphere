package com.shopsphere.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

/**
 * SimilarProduct - Stores similar products relationships
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "similar_products")
public class SimilarProduct {

    @Id
    private String id;

    private String sourceProductId;
    private String similarProductId;
    private Double similarityScore;    // 0-100
    private String categoryMatch;       // Category of both (must be same)
    private String priceRange;          // Similar price range
    private Map<String, Object> matchedAttributes;  // e.g., brand, color, etc.
    private Instant lastUpdated;
}
