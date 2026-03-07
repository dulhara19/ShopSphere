package com.shopsphere.review.dto;

import com.shopsphere.review.model.ReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class FeaturedReviewResponse {
    private String id;
    private String productId;
    private String userId;
    private int rating;
    private String title;
    private String body;
    private ReviewStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private int helpfulCount;
    private boolean featured;
    private String featuredReason;
}
