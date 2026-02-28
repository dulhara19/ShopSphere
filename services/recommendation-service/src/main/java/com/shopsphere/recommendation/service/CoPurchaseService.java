package com.shopsphere.recommendation.service;

import com.shopsphere.recommendation.model.CoPurchaseMatrix;
import com.shopsphere.recommendation.model.RecommendationEvent;
import com.shopsphere.recommendation.model.EventType;
import com.shopsphere.recommendation.repository.CoPurchaseMatrixRepository;
import com.shopsphere.recommendation.repository.EventTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Epic 1.5: Customers Also Bought Service
 * Builds co-purchase matrix from order history
 * Suggests products bought together and bundles
 */
@Service
@RequiredArgsConstructor
public class CoPurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(CoPurchaseService.class);

    private final CoPurchaseMatrixRepository coPurchaseRepository;
    private final EventTrackingRepository eventTrackingRepository;

    /**
     * Recalculate co-purchase matrix (scheduled daily)
     */
    @Scheduled(fixedDelay = 86400000) // 24 hours
    public void recalculateCoPurchaseMatrix() {
        logger.info("Starting co-purchase matrix recalculation...");

        try {
            // Get purchase events from last 90 days
            Instant ninetyDaysAgo = Instant.now().minus(90, ChronoUnit.DAYS);

            Map<String, Map<String, Integer>> coOccurrenceMap = buildCoOccurrenceMap(ninetyDaysAgo);

            // Delete existing matrix
            coPurchaseRepository.deleteAll();

            // Create new matrix entries
            List<CoPurchaseMatrix> matrix = new ArrayList<>();

            for (Map.Entry<String, Map<String, Integer>> entry : coOccurrenceMap.entrySet()) {
                String productId = entry.getKey();
                Map<String, Integer> associatedProducts = entry.getValue();

                for (Map.Entry<String, Integer> associated : associatedProducts.entrySet()) {
                    int count = associated.getValue();
                    Double score = Math.log(count + 1); // Logarithmic scaling

                    CoPurchaseMatrix record = CoPurchaseMatrix.builder()
                        .productId(productId)
                        .associatedProductId(associated.getKey())
                        .purchaseCount(count)
                        .coOccurrenceScore(score)
                        .lastUpdated(Instant.now())
                        .build();

                    matrix.add(record);
                }
            }

            if (!matrix.isEmpty()) {
                coPurchaseRepository.saveAll(matrix);
                logger.info("Saved {} co-purchase relationships", matrix.size());
            }

            logger.info("Co-purchase matrix recalculation completed");
        } catch (Exception e) {
            logger.error("Error recalculating co-purchase matrix", e);
        }
    }

    /**
     * Get products frequently bought with a given product
     * @param productId Product to find companions for
     * @param limit Number of results
     * @return List of frequently bought together products
     */
    public List<CoPurchaseMatrix> getAlsoBoughtProducts(String productId, int limit) {
        logger.debug("Getting {} products bought with productId={}", limit, productId);

        Pageable pageable = PageRequest.of(0, limit);
        return coPurchaseRepository.findByProductIdOrderByCoOccurrenceScoreDesc(productId, pageable);
    }

    /**
     * Get bundle suggestions for a product
     * @param productId Product to create bundle for
     * @param bundleSize Number of products in bundle
     * @return List of products to bundle
     */
    public List<CoPurchaseMatrix> getBundleSuggestions(String productId, int bundleSize) {
        logger.debug("Getting bundle suggestions for productId={}, size={}", productId, bundleSize);

        Pageable pageable = PageRequest.of(0, bundleSize);
        return coPurchaseRepository.findByProductIdOrderByCoOccurrenceScoreDesc(productId, pageable);
    }

    /**
     * Build co-occurrence map from purchase events
     */
    private Map<String, Map<String, Integer>> buildCoOccurrenceMap(Instant sinceTime) {
        Map<String, Map<String, Integer>> coOccurrence = new HashMap<>();

        // Get all purchase events
        List<RecommendationEvent> events = eventTrackingRepository.findAll();

        // Map orders to products
        Map<String, List<String>> orderProducts = new HashMap<>();

        for (RecommendationEvent event : events) {
            if (event.getTimestamp().isBefore(sinceTime)) continue;
            if (!event.getEventType().equals(EventType.PURCHASE)) continue;

            String orderId = event.getMetadata() != null ? (String) event.getMetadata().get("orderId") : null;
            String productId = event.getProductId();

            if (orderId != null && productId != null) {
                orderProducts.computeIfAbsent(orderId, k -> new ArrayList<>()).add(productId);
            }
        }

        // Build co-occurrence relationships
        for (List<String> products : orderProducts.values()) {
            for (int i = 0; i < products.size(); i++) {
                for (int j = 0; j < products.size(); j++) {
                    if (i != j) {
                        String product1 = products.get(i);
                        String product2 = products.get(j);

                        coOccurrence.computeIfAbsent(product1, k -> new HashMap<>())
                            .merge(product2, 1, Integer::sum);
                    }
                }
            }
        }

        return coOccurrence;
    }
}
