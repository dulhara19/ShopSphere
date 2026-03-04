// ...existing code...
package com.shopsphere.review.dto;

import com.shopsphere.review.model.ReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ReviewResponse {
    private String id;
    private String productId;
    private String userId;
    private int rating;
    private String title;
    private String body;
    private ReviewStatus status;
    private Instant createdAt;
}
// ...existing code...
