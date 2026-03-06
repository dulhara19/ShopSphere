package com.shopsphere.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSummaryDTO implements Serializable {
    private Double averageRating;
    private Integer totalReviews;
}