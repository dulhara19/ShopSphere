package com.shopsphere.recommendation.repository;

import com.shopsphere.recommendation.model.TrendingProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrendingProductRepository extends MongoRepository<TrendingProduct, String> {

    // Get top global trending products
    List<TrendingProduct> findByCategoryIdIsNullOrderByTrendingScoreDescRankAsc(
            org.springframework.data.domain.Pageable pageable);

    // Get top trending products by category
    List<TrendingProduct> findByCategoryIdOrderByTrendingScoreDescRankAsc(String categoryId,
            org.springframework.data.domain.Pageable pageable);

    // Find trending product by productId (global)
    Optional<TrendingProduct> findByProductIdAndCategoryIdIsNull(String productId);

    // Find trending product by productId and category
    Optional<TrendingProduct> findByProductIdAndCategoryId(String productId, String categoryId);

    // Delete by categoryId (for updates)
    long deleteByCategoryIdIsNull();

    long deleteByCategoryId(String categoryId);
}
