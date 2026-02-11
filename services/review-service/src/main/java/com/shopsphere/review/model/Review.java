// ...existing code...
package com.shopsphere.review.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Document(collection = "reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    private String id;

    @NotBlank
    private String productId;

    @NotBlank
    private String userId;

    @Min(1)
    @Max(5)
    private int rating;

    private String title;

    private String body;

    @NotNull
    private ReviewStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Builder.Default
    private int helpfulCount = 0;

    @Builder.Default
    private boolean deleted = false;
}
// ...existing code...
