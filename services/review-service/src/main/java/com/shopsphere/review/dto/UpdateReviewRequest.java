package com.shopsphere.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateReviewRequest {

    @Min(1)
    @Max(5)
    private Integer rating;

    private String title;

    private String content;

    private java.util.List<String> images;
}
