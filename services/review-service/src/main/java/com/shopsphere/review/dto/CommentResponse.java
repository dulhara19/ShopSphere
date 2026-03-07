package com.shopsphere.review.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CommentResponse {
    private String id;
    private String reviewId;
    private String userId;
    private String content;
    private Instant createdAt;
}
