package com.shopsphere.review.controller;

import com.shopsphere.review.dto.*;
import com.shopsphere.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsForProduct(@PathVariable String productId, Pageable pageable) {
        Page<ReviewResponse> page = reviewService.getReviewsForProduct(productId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable String reviewId) {
        ReviewResponse response = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(response);
    }

    // 1. PUT /{reviewId} — Update a review
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable String reviewId,
                                                       @Valid @RequestBody UpdateReviewRequest request) {
        ReviewResponse response = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(response);
    }

    // 2. DELETE /{reviewId} — Soft delete a review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    // 3. GET /user/me — Get reviews by user
    @GetMapping("/user/me")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByUser(@RequestParam String userId, Pageable pageable) {
        Page<ReviewResponse> page = reviewService.getReviewsByUser(userId, pageable);
        return ResponseEntity.ok(page);
    }

    // 4. GET /product/{productId}/summary — Rating summary
    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<RatingSummaryResponse> getRatingSummary(@PathVariable String productId) {
        RatingSummaryResponse response = reviewService.getRatingSummary(productId);
        return ResponseEntity.ok(response);
    }

    // 5. GET /product/{productId}/featured — Featured/top reviews
    @GetMapping("/product/{productId}/featured")
    public ResponseEntity<List<FeaturedReviewResponse>> getFeaturedReviews(@PathVariable String productId) {
        List<FeaturedReviewResponse> response = reviewService.getFeaturedReviews(productId);
        return ResponseEntity.ok(response);
    }

    // 6. POST /{reviewId}/helpful — Mark review as helpful
    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<Void> markHelpful(@PathVariable String reviewId) {
        reviewService.markHelpful(reviewId);
        return ResponseEntity.ok().build();
    }

    // 7. DELETE /{reviewId}/helpful — Remove helpful mark
    @DeleteMapping("/{reviewId}/helpful")
    public ResponseEntity<Void> removeHelpful(@PathVariable String reviewId) {
        reviewService.removeHelpful(reviewId);
        return ResponseEntity.ok().build();
    }

    // 8. POST /{reviewId}/comments — Add comment to review
    @PostMapping("/{reviewId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable String reviewId,
                                                      @Valid @RequestBody AddCommentRequest request) {
        CommentResponse response = reviewService.addComment(reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 9. DELETE /{reviewId}/comments/{commentId} — Delete comment
    @DeleteMapping("/{reviewId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String reviewId,
                                              @PathVariable String commentId) {
        reviewService.deleteComment(reviewId, commentId);
        return ResponseEntity.noContent().build();
    }
}
