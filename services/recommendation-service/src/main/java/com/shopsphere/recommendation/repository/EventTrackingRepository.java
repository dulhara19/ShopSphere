package com.shopsphere.recommendation.repository;

import com.shopsphere.recommendation.model.RecommendationEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTrackingRepository extends MongoRepository<RecommendationEvent, String> {
    // You can add custom queries here if needed
}
