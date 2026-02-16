// ...existing code...
package com.shopsphere.review.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
public class ReviewRequest {

    @NotBlank
    private String productId;

    @NotBlank
    private String userId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String title;

    private String body;
}
// ...existing code...
