package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.model.SocialMetric;
import com.shopsphere.analytics.service.SocialAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class SocialAnalyticsController {

    private final SocialAnalyticsService socialAnalyticsService;

    @GetMapping("/reviews/summary")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getReviewSummary(
            @RequestParam(name = "from") String from,
            @RequestParam(name = "to") String to) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        return ResponseEntity.ok(ApiResponseDTO.success(
                socialAnalyticsService.getReviewSummary(fromDate, toDate),
                "Review summary retrieved"));
    }

    @GetMapping("/social/engagement")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getSocialEngagement(
            @RequestParam(name = "from") String from,
            @RequestParam(name = "to") String to) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        return ResponseEntity.ok(ApiResponseDTO.success(
                socialAnalyticsService.getSocialEngagement(fromDate, toDate),
                "Social engagement retrieved"));
    }

    @GetMapping("/influencers/performance")
    public ResponseEntity<ApiResponseDTO<List<SocialMetric>>> getInfluencerPerformance() {
        return ResponseEntity.ok(ApiResponseDTO.success(
                socialAnalyticsService.getInfluencerPerformance(),
                "Influencer performance retrieved"));
    }
}
