package com.shopsphere.recommendation.service;

import com.shopsphere.recommendation.model.RecommendationAnalytics;
import com.shopsphere.recommendation.repository.RecommendationAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Epic 2.6: Recommendation Analytics Service
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    private final RecommendationAnalyticsRepository analyticsRepository;

    public RecommendationAnalytics trackEvent(String userId, String recommendationId, String type, String metadata) {
        RecommendationAnalytics event = RecommendationAnalytics.builder()
                .userId(userId)
                .recommendationId(recommendationId)
                .type(type)
                .timestamp(Instant.now())
                .metadata(metadata)
                .build();
        return analyticsRepository.save(event);
    }

    public List<RecommendationAnalytics> getUserAnalytics(String userId) {
        return analyticsRepository.findByUserId(userId);
    }

    public List<RecommendationAnalytics> getRecommendationAnalytics(String recommendationId) {
        return analyticsRepository.findByRecommendationId(recommendationId);
    }

    public List<RecommendationAnalytics> getAllAnalytics() {
        return analyticsRepository.findAll();
    }
}
