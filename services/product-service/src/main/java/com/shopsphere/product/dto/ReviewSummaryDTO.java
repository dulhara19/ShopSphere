package com.shopsphere.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Map; // Added for Story 2.5.3

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSummaryDTO implements Serializable {
    private Double averageRating;
    private Integer totalReviews;
    
    // Story 2.5.3: Rating breakdown (e.g., {"5": 120, "4": 45, "3": 10, "2": 2, "1": 5})
    private Map<String, Integer> ratingBreakdown;
}