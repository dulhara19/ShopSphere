package com.shopsphere.recommendation.repository;

import com.shopsphere.recommendation.model.RecommendationAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationAnalyticsRepository extends MongoRepository<RecommendationAnalytics, String> {
    List<RecommendationAnalytics> findByUserId(String userId);
    List<RecommendationAnalytics> findByRecommendationId(String recommendationId);
}
