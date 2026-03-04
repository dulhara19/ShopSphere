package com.shopsphere.review.repository;

import com.shopsphere.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review, String> {

    /**
     * Find non-deleted reviews for a product with pagination.
     */
    Page<Review> findByProductIdAndDeletedFalse(String productId, Pageable pageable);

    /**
     * Find a single non-deleted review by id.
     */
    Optional<Review> findByIdAndDeletedFalse(String id);

    /**
     * Helper to fetch all non-deleted reviews for a product.
     */
    List<Review> findByProductIdAndDeletedFalse(String productId);
}

