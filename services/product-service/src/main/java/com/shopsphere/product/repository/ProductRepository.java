package com.shopsphere.product.repository;

import com.shopsphere.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

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
     */
    long countByCategoryId(String categoryId);

    /**
     * Story 1.3.1: Get all products by status with pagination
     */
    Page<Product> findByStatus(String status, Pageable pageable);

    /**
     * Story 1.3.2: Filter products by a list of category IDs and status
     * This is used to fetch products belonging to a category and all its subcategories.
     */
    Page<Product> findByCategoryIdInAndStatus(List<String> categoryIds, String status, Pageable pageable);
}