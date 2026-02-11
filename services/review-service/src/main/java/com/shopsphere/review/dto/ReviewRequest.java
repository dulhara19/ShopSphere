// ...existing code...
package com.shopsphere.review.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
