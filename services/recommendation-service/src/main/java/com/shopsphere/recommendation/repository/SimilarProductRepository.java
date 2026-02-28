package com.shopsphere.recommendation.repository;

import com.shopsphere.recommendation.model.SimilarProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimilarProductRepository extends MongoRepository<SimilarProduct, String> {

    // Find similar products for a given source product
    List<SimilarProduct> findBySourceProductIdOrderBySimilarityScoreDesc(String sourceProductId);

    // Find similar products with limit
    List<SimilarProduct> findBySourceProductIdOrderBySimilarityScoreDesc(String sourceProductId, org.springframework.data.domain.Pageable pageable);

    // Delete all similarity records for a product
    long deleteBySourceProductId(String productId);
}
