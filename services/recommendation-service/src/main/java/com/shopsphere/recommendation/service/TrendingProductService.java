package com.shopsphere.recommendation.service;

import com.shopsphere.recommendation.model.TrendingProduct;
import com.shopsphere.recommendation.model.RecommendationEvent;
import com.shopsphere.recommendation.model.EventType;
import com.shopsphere.recommendation.repository.TrendingProductRepository;
import com.shopsphere.recommendation.repository.EventTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Epic 1.3: Trending Products Service
 * Calculates trending products based on views, cart adds, and purchases
 * Scores are time-weighted (recent = higher)
 */
@Service
@RequiredArgsConstructor
public class TrendingProductService {

    private static final Logger logger = LoggerFactory.getLogger(TrendingProductService.class);

    private final TrendingProductRepository trendingProductRepository;
    private final EventTrackingRepository eventTrackingRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Recalculate trending scores (scheduled every hour)
     */
    @Scheduled(fixedDelay = 3600000) // 1 hour
    public void recalculateTrendingProducts() {
        logger.info("Starting trending products recalculation...");

        try {
            // Get events from last 7 days
            Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);

            // Calculate global trending
            calculateGlobalTrending(sevenDaysAgo);

            // Calculate category-wise trending
            calculateCategoryTrending(sevenDaysAgo);

            logger.info("Trending products recalculation completed successfully");
        } catch (Exception e) {
            logger.error("Error recalculating trending products", e);
        }
    }

    /**
     * Calculate global trending products
     */
    private void calculateGlobalTrending(Instant sinceTime) {
        // Aggregate events
        Map<String, TrendingStats> productStats = aggregateProductEvents(sinceTime, null);

        // Clear existing global trending
        trendingProductRepository.deleteByCategoryIdIsNull();

        // Create new trending products
        List<TrendingProduct> trendingProducts = new ArrayList<>();
        int rank = 1;

        for (Map.Entry<String, TrendingStats> entry : productStats.entrySet()) {
            TrendingStats stats = entry.getValue();
            Double score = TrendingProduct.calculateTrendingScore(
                stats.viewCount,
                stats.cartAddCount,
                stats.purchaseCount,
                Instant.now()
            );

            TrendingProduct trending = TrendingProduct.builder()
                .productId(entry.getKey())
                .categoryId(null)  // Global trending
                .trendingScore(score)
                .viewCount(stats.viewCount)
                .cartAddCount(stats.cartAddCount)
                .purchaseCount(stats.purchaseCount)
                .lastUpdated(Instant.now())
                .rank(rank++)
                .build();

            trendingProducts.add(trending);
        }

        if (!trendingProducts.isEmpty()) {
            trendingProductRepository.saveAll(trendingProducts);
            logger.info("Saved {} global trending products", trendingProducts.size());
        }
    }

    /**
     * Calculate category-wise trending products
     */
    private void calculateCategoryTrending(Instant sinceTime) {
        // Get all unique categories from events
        Set<String> categories = getAllCategoriesFromEvents();

        for (String categoryId : categories) {
            Map<String, TrendingStats> productStats = aggregateProductEvents(sinceTime, categoryId);

            // Delete existing category trending
            trendingProductRepository.deleteByCategoryId(categoryId);

            // Create new trending products
            List<TrendingProduct> trendingProducts = new ArrayList<>();
            int rank = 1;

            for (Map.Entry<String, TrendingStats> entry : productStats.entrySet()) {
                TrendingStats stats = entry.getValue();
                Double score = TrendingProduct.calculateTrendingScore(
                    stats.viewCount,
                    stats.cartAddCount,
                    stats.purchaseCount,
                    Instant.now()
                );

                TrendingProduct trending = TrendingProduct.builder()
                    .productId(entry.getKey())
                    .categoryId(categoryId)
                    .trendingScore(score)
                    .viewCount(stats.viewCount)
                    .cartAddCount(stats.cartAddCount)
                    .purchaseCount(stats.purchaseCount)
                    .lastUpdated(Instant.now())
                    .rank(rank++)
                    .build();

                trendingProducts.add(trending);
            }

            if (!trendingProducts.isEmpty()) {
                trendingProductRepository.saveAll(trendingProducts);
                logger.info("Saved {} trending products for category {}", trendingProducts.size(), categoryId);
            }
        }
    }

    /**
     * Get top global trending products
     */
    public List<TrendingProduct> getGlobalTrendingProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return trendingProductRepository.findByCategoryIdIsNullOrderByTrendingScoreDescRankAsc(pageable);
    }

    /**
     * Get top trending products by category
     */
    public List<TrendingProduct> getCategoryTrendingProducts(String categoryId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return trendingProductRepository.findByCategoryIdOrderByTrendingScoreDescRankAsc(categoryId, pageable);
    }

    /**
     * Aggregate events by product and calculate stats
     */
    private Map<String, TrendingStats> aggregateProductEvents(Instant sinceTime, String categoryId) {
        Map<String, TrendingStats> stats = new HashMap<>();

        List<RecommendationEvent> events = eventTrackingRepository.findAll();

        for (RecommendationEvent event : events) {
            if (event.getTimestamp().isBefore(sinceTime)) continue;

            // Filter by category if provided
            if (categoryId != null && event.getMetadata() != null) {
                String eventCategory = (String) event.getMetadata().get("categoryId");
                if (!categoryId.equals(eventCategory)) continue;
            }

            String productId = event.getProductId();
            if (productId == null) continue;

            TrendingStats stat = stats.getOrDefault(productId, new TrendingStats());

            switch (event.getEventType()) {
                case PRODUCT_VIEW:
                    stat.viewCount++;
                    break;
                case ADD_TO_CART:
                    stat.cartAddCount++;
                    break;
                case PURCHASE:
                    stat.purchaseCount++;
                    break;
                default:
                    break;
            }

            stats.put(productId, stat);
        }

        return stats;
    }

    /**
     * Get all unique categories from events
     */
    private Set<String> getAllCategoriesFromEvents() {
        Set<String> categories = new HashSet<>();
        List<RecommendationEvent> events = eventTrackingRepository.findAll();

        for (RecommendationEvent event : events) {
            if (event.getMetadata() != null) {
                Object categoryId = event.getMetadata().get("categoryId");
                if (categoryId != null) {
                    categories.add((String) categoryId);
                }
            }
        }

        return categories;
    }

    /**
     * Helper class to hold trending stats
     */
    private static class TrendingStats {
        long viewCount = 0;
        long cartAddCount = 0;
        long purchaseCount = 0;
    }
}
