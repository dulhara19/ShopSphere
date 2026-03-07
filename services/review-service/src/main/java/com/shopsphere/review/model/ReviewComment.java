package com.shopsphere.review.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "review_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewComment {

    @Id
    private String id;

    private String reviewId;

    private String userId;

    private String content;

    @CreatedDate
    private Instant createdAt;
}
