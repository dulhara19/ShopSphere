package com.shopsphere.recommendation.repository;

import com.shopsphere.recommendation.model.VisualSearchImage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisualSearchImageRepository extends MongoRepository<VisualSearchImage, String> {
    List<VisualSearchImage> findByProductId(String productId);
}
