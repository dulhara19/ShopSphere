package com.shopsphere.review.service;

import com.shopsphere.review.dto.ReviewRequest;
import com.shopsphere.review.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request);

    Page<ReviewResponse> getReviewsForProduct(String productId, Pageable pageable);

    ReviewResponse getReviewById(String id);
}
