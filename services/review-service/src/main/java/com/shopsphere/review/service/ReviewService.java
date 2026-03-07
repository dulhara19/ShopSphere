package com.shopsphere.review.service;

import com.shopsphere.review.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request);

    Page<ReviewResponse> getReviewsForProduct(String productId, Pageable pageable);

    ReviewResponse getReviewById(String id);

    ReviewResponse updateReview(String reviewId, UpdateReviewRequest request);

    void deleteReview(String reviewId);

    Page<ReviewResponse> getReviewsByUser(String userId, Pageable pageable);

    RatingSummaryResponse getRatingSummary(String productId);

    List<FeaturedReviewResponse> getFeaturedReviews(String productId);

    void markHelpful(String reviewId);

    void removeHelpful(String reviewId);

    CommentResponse addComment(String reviewId, AddCommentRequest request);

    void deleteComment(String reviewId, String commentId);
}
