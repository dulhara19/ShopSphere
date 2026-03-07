package com.shopsphere.review.service.impl;

import com.shopsphere.review.dto.*;
import com.shopsphere.review.exception.NotFoundException;
import com.shopsphere.review.model.Review;
import com.shopsphere.review.model.ReviewComment;
import com.shopsphere.review.model.ReviewStatus;
import com.shopsphere.review.repository.ReviewCommentRepository;
import com.shopsphere.review.repository.ReviewRepository;
import com.shopsphere.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewCommentRepository reviewCommentRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        Review review = Review.builder()
                .productId(request.getProductId())
                .userId(request.getUserId())
                .rating(request.getRating())
                .title(request.getTitle())
                .body(request.getBody())
                .status(ReviewStatus.PENDING)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .deleted(false)
                .helpfulCount(0)
                .build();

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    @Override
    public Page<ReviewResponse> getReviewsForProduct(String productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndDeletedFalse(productId, pageable)
                .map(this::toResponse);
    }

    @Override
    public ReviewResponse getReviewById(String id) {
        Review r = findReviewOrThrow(id);
        return toResponse(r);
    }

    @Override
    public ReviewResponse updateReview(String reviewId, UpdateReviewRequest request) {
        Review review = findReviewOrThrow(reviewId);

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            review.setBody(request.getContent());
        }
        review.setUpdatedAt(Instant.now());

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    @Override
    public void deleteReview(String reviewId) {
        Review review = findReviewOrThrow(reviewId);
        review.setDeleted(true);
        reviewRepository.save(review);
    }

    @Override
    public Page<ReviewResponse> getReviewsByUser(String userId, Pageable pageable) {
        return reviewRepository.findByUserIdAndDeletedFalse(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    public RatingSummaryResponse getRatingSummary(String productId) {
        List<Review> reviews = reviewRepository.findByProductIdAndDeletedFalse(productId);

        Map<String, Long> distribution = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(String.valueOf(i), 0L);
        }

        double sum = 0;
        for (Review r : reviews) {
            sum += r.getRating();
            String key = String.valueOf(r.getRating());
            distribution.put(key, distribution.get(key) + 1);
        }

        double avg = reviews.isEmpty() ? 0.0 : sum / reviews.size();

        return RatingSummaryResponse.builder()
                .productId(productId)
                .averageRating(Math.round(avg * 10.0) / 10.0)
                .totalReviews(reviews.size())
                .distribution(distribution)
                .verifiedPurchaseCount(0)
                .build();
    }

    @Override
    public List<FeaturedReviewResponse> getFeaturedReviews(String productId) {
        List<Review> reviews = reviewRepository.findByProductIdAndDeletedFalse(productId);

        return reviews.stream()
                .sorted(Comparator.comparingInt(Review::getHelpfulCount).reversed())
                .limit(3)
                .map(r -> FeaturedReviewResponse.builder()
                        .id(r.getId())
                        .productId(r.getProductId())
                        .userId(r.getUserId())
                        .rating(r.getRating())
                        .title(r.getTitle())
                        .body(r.getBody())
                        .status(r.getStatus())
                        .createdAt(r.getCreatedAt())
                        .updatedAt(r.getUpdatedAt())
                        .helpfulCount(r.getHelpfulCount())
                        .featured(true)
                        .featuredReason("most_helpful")
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void markHelpful(String reviewId) {
        Review review = findReviewOrThrow(reviewId);
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    @Override
    public void removeHelpful(String reviewId) {
        Review review = findReviewOrThrow(reviewId);
        review.setHelpfulCount(Math.max(0, review.getHelpfulCount() - 1));
        reviewRepository.save(review);
    }

    @Override
    public CommentResponse addComment(String reviewId, AddCommentRequest request) {
        // Verify review exists
        findReviewOrThrow(reviewId);

        ReviewComment comment = ReviewComment.builder()
                .reviewId(reviewId)
                .userId(request.getUserId())
                .content(request.getContent())
                .createdAt(Instant.now())
                .build();

        ReviewComment saved = reviewCommentRepository.save(comment);

        return CommentResponse.builder()
                .id(saved.getId())
                .reviewId(saved.getReviewId())
                .userId(saved.getUserId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public void deleteComment(String reviewId, String commentId) {
        // Verify review exists
        findReviewOrThrow(reviewId);

        if (!reviewCommentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment not found with id: " + commentId);
        }
        reviewCommentRepository.deleteById(commentId);
    }

    private Review findReviewOrThrow(String id) {
        return reviewRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Review not found with id: " + id));
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .productId(r.getProductId())
                .userId(r.getUserId())
                .rating(r.getRating())
                .title(r.getTitle())
                .body(r.getBody())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .helpfulCount(r.getHelpfulCount())
                .build();
    }
}
