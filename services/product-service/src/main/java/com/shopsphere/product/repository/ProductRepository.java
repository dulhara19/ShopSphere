package com.shopsphere.product.repository;

import com.shopsphere.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
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
     */
    Page<Product> findByCategoryIdInAndStatus(List<String> categoryIds, String status, Pageable pageable);

    /**
     * Story 1.3.3: Filter products by status and price range
     */
    Page<Product> findByStatusAndPriceBetween(String status, Double minPrice, Double maxPrice, Pageable pageable);

    /**
     * Story 1.3.3: Filter products by category list, status, and price range
     */
    Page<Product> findByCategoryIdInAndStatusAndPriceBetween(
            List<String> categoryIds, String status, Double minPrice, Double maxPrice, Pageable pageable);

    /**
     * Story 1.3.4: Comprehensive search including text keyword, category list, and price range.
     * $regex: ?0 -> Search keyword in name or description (case-insensitive)
     * $in: ?1    -> Match any category ID in the list
     * $gte: ?2   -> Minimum price
     * $lte: ?3   -> Maximum price
     */
    @Query("{ '$and': [ " +
           "{ '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'description': { '$regex': ?0, '$options': 'i' } } ] }, " +
           "{ 'categoryId': { '$in': ?1 } }, " +
           "{ 'price': { '$gte': ?2, '$lte': ?3 } }, " +
           "{ 'status': 'ACTIVE' } " +
           "] }")
    Page<Product> searchProductsWithCategory(String keyword, List<String> categoryIds, Double minPrice, Double maxPrice, Pageable pageable);

    /**
     * Story 1.3.4: Comprehensive search including text keyword and price range (without category filter).
     * Used when the user performs a global search without selecting a specific category.
     */
    @Query("{ '$and': [ " +
           "{ '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'description': { '$regex': ?0, '$options': 'i' } } ] }, " +
           "{ 'price': { '$gte': ?1, '$lte': ?2 } }, " +
           "{ 'status': 'ACTIVE' } " +
           "] }")
    Page<Product> searchProductsGlobal(String keyword, Double minPrice, Double maxPrice, Pageable pageable);
}