// ...existing code...
package com.shopsphere.review.controller;

import com.shopsphere.review.dto.ReviewRequest;
import com.shopsphere.review.dto.ReviewResponse;
import com.shopsphere.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
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
}
// ...existing code...
