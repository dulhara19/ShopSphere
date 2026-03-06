package com.shopsphere.analytics.service;

import com.shopsphere.analytics.model.SocialMetric;
import com.shopsphere.analytics.repository.SocialMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialAnalyticsService {

    private final SocialMetricRepository socialMetricRepository;

    public Map<String, Object> getReviewSummary(LocalDate from, LocalDate to) {
        List<SocialMetric> metrics = socialMetricRepository.findByMetricDateBetween(from, to);

        long totalReviews = metrics.stream().mapToLong(m -> m.getTotalReviews() != null ? m.getTotalReviews() : 0)
                .sum();
        long positive = metrics.stream()
                .mapToLong(m -> m.getPositiveSentiments() != null ? m.getPositiveSentiments() : 0).sum();
        long negative = metrics.stream()
                .mapToLong(m -> m.getNegativeSentiments() != null ? m.getNegativeSentiments() : 0).sum();

        double avgRating = metrics.stream()
                .filter(m -> m.getAverageRating() != null)
                .mapToDouble(SocialMetric::getAverageRating)
                .average().orElse(0.0);

        return Map.of(
                "totalReviews", totalReviews,
                "averageRating", avgRating,
                "sentiment", Map.of(
                        "positive", positive,
                        "negative", negative));
    }

    public Map<String, Object> getSocialEngagement(LocalDate from, LocalDate to) {
        List<SocialMetric> metrics = socialMetricRepository.findByMetricDateBetween(from, to);
        long interactions = metrics.stream()
                .mapToLong(m -> (m.getLikes() != null ? m.getLikes() : 0) +
                        (m.getShares() != null ? m.getShares() : 0) +
                        (m.getComments() != null ? m.getComments() : 0))
                .sum();

        return Map.of(
                "totalInteractions", interactions,
                "period", from.toString() + " to " + to.toString());
    }

    public List<SocialMetric> getInfluencerPerformance() {
        return socialMetricRepository.findTopByOrderByAttributedSalesDesc()
                .stream().limit(10).collect(Collectors.toList());
    }
}
