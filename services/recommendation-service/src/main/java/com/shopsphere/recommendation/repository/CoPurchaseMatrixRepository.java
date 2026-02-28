package com.shopsphere.recommendation.repository;

import com.shopsphere.recommendation.model.CoPurchaseMatrix;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoPurchaseMatrixRepository extends MongoRepository<CoPurchaseMatrix, String> {

    // Find products frequently bought with a given product
    List<CoPurchaseMatrix> findByProductIdOrderByCoOccurrenceScoreDesc(String productId);

    // Find products with limit
    List<CoPurchaseMatrix> findByProductIdOrderByCoOccurrenceScoreDesc(String productId, org.springframework.data.domain.Pageable pageable);

    // Delete all co-purchase records for a product
    long deleteByProductId(String productId);
}
