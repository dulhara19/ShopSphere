package com.shopsphere.product.repository;

import com.shopsphere.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    /**
     * Story 1.1.5: List seller's products with pagination and status filtering
     */
    Page<Product> findBySellerIdAndStatus(String sellerId, String status, Pageable pageable);

    /**
     * Story 1.1.5: List all products for a specific seller with pagination
     */
    Page<Product> findBySellerId(String sellerId, Pageable pageable);

    /**
     * Story 1.2.2: Get the total number of products in a specific category
     * Category list එක පෙන්වන විට එක් එක් category එකට අදාළ product count එක පෙන්වීමට මෙය භාවිතා කරයි
     */
    long countByCategoryId(String categoryId);
}