package com.shopsphere.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * UserPreference - Stores user preferences for personalization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_preferences")
public class UserPreference {

    @Id
    private String id;

    private String userId;
    private Map<String, Integer> categoryAffinity;  // Category ID -> Score
    private Integer minPricePreference;             // Minimum typical price
    private Integer maxPricePreference;             // Maximum typical price
    private Integer totalViewCount;
    private Integer totalPurchaseCount;
    private Instant lastUpdated;

    public void addCategoryView(String categoryId) {
        if (categoryAffinity == null) {
            categoryAffinity = new HashMap<>();
        }
        categoryAffinity.put(categoryId, categoryAffinity.getOrDefault(categoryId, 0) + 1);
    }

    public void addPurchase() {
        if (totalPurchaseCount == null) {
            totalPurchaseCount = 0;
        }
        totalPurchaseCount++;
    }

    public void addView() {
        if (totalViewCount == null) {
            totalViewCount = 0;
        }
        totalViewCount++;
    }
}
