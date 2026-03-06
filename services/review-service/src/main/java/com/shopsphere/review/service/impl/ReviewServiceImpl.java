// ...existing code...
package com.shopsphere.review.service.impl;

import com.shopsphere.review.dto.ReviewRequest;
import com.shopsphere.review.dto.ReviewResponse;
import com.shopsphere.review.exception.NotFoundException;
import com.shopsphere.review.model.Review;
import com.shopsphere.review.model.ReviewStatus;
import com.shopsphere.review.repository.ReviewRepository;
import com.shopsphere.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

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

        return ReviewResponse.builder()
                .id(saved.getId())
                .productId(saved.getProductId())
                .userId(saved.getUserId())
                .rating(saved.getRating())
                .title(saved.getTitle())
                .body(saved.getBody())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public Page<ReviewResponse> getReviewsForProduct(String productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndDeletedFalse(productId, pageable)
                .map(r -> ReviewResponse.builder()
                        .id(r.getId())
                        .productId(r.getProductId())
                        .userId(r.getUserId())
                        .rating(r.getRating())
                        .title(r.getTitle())
                        .body(r.getBody())
                        .status(r.getStatus())
                        .createdAt(r.getCreatedAt())
                        .build());
    }

    @Override
    public ReviewResponse getReviewById(String id) {
        Review r = reviewRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Review not found with id: " + id));

        return ReviewResponse.builder()
                .id(r.getId())
                .productId(r.getProductId())
                .userId(r.getUserId())
                .rating(r.getRating())
                .title(r.getTitle())
                .body(r.getBody())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
// ...existing code...
