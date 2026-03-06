package com.shopsphere.recommendation.repository;

import com.shopsphere.recommendation.model.ProductEmbedding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductEmbeddingRepository extends MongoRepository<ProductEmbedding, String> {
    Optional<ProductEmbedding> findByProductId(String productId);
}
