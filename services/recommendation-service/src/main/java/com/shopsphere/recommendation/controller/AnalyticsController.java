package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.model.RecommendationAnalytics;
import com.shopsphere.recommendation.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/recommendations")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics")
    public List<RecommendationAnalytics> getAnalytics(@RequestParam(required = false) String userId,
                                                      @RequestParam(required = false) String recommendationId) {
        if (userId != null) {
            return analyticsService.getUserAnalytics(userId);
        }
        if (recommendationId != null) {
            return analyticsService.getRecommendationAnalytics(recommendationId);
        }
        return analyticsService.getAllAnalytics();
    }

    @GetMapping("/ab-tests")
    public List<String> getABTests() {
        // stub endpoint returning static info
        return List.of("cf-v1", "cf-v2");
    }
}
