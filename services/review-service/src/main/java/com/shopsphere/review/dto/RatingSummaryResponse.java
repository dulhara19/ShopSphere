package com.shopsphere.review.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RatingSummaryResponse {
    private String productId;
    private double averageRating;
    private long totalReviews;
    private Map<String, Long> distribution;
    private long verifiedPurchaseCount;
}
