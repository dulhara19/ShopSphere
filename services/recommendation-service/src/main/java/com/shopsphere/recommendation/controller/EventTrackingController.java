package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.dto.AddToCartRequest;
import com.shopsphere.recommendation.dto.ProductViewRequest;
import com.shopsphere.recommendation.dto.PurchaseRequest;
import com.shopsphere.recommendation.dto.SearchQueryRequest;
import com.shopsphere.recommendation.model.RecommendationEvent;
import com.shopsphere.recommendation.repository.EventTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Epic 1.1: User Behavior Tracking
 * Tracks all user interactions: views, searches, cart, purchases
 */
@RestController
@RequestMapping("/api/events")
public class EventTrackingController {

    private static final Logger logger = LoggerFactory.getLogger(EventTrackingController.class);

    @Autowired
    private EventTrackingRepository repository;

    /**
     * Epic 1.1.1: Track product views
     * POST /api/events/product-view
     */
    @PostMapping("/product-view")
    public RecommendationEvent productView(@Valid @RequestBody ProductViewRequest request) {
        logger.info("Tracking PRODUCT_VIEW: productId={}, userId={}, sessionId={}",
            request.getProductId(), request.getUserId(), request.getSessionId());

        RecommendationEvent event = RecommendationEvent.productView(
            request.getProductId(),
            request.getUserId(),
            request.getSessionId(),
            request.getMetadata()
        );
        return repository.save(event);
    }

    /**
     * Epic 1.1.2: Track search queries
     * POST /api/events/search-query
     */
    @PostMapping("/search-query")
    public RecommendationEvent searchQuery(@Valid @RequestBody SearchQueryRequest request) {
        logger.info("Tracking SEARCH_QUERY: searchTerm={}, userId={}, sessionId={}",
            request.getSearchTerm(), request.getUserId(), request.getSessionId());

        RecommendationEvent event = RecommendationEvent.searchQuery(
            request.getUserId(),
            request.getSessionId(),
            request.getSearchTerm(),
            request.getMetadata()
        );
        return repository.save(event);
    }

    /**
     * Epic 1.1.3: Track cart additions
     * POST /api/events/add-to-cart
     */
    @PostMapping("/add-to-cart")
    public RecommendationEvent addToCart(@Valid @RequestBody AddToCartRequest request) {
        logger.info("Tracking ADD_TO_CART: productId={}, userId={}, sessionId={}, quantity={}",
            request.getProductId(), request.getUserId(), request.getSessionId(), request.getQuantity());

        RecommendationEvent event = RecommendationEvent.addToCart(
            request.getProductId(),
            request.getUserId(),
            request.getSessionId(),
            request.getQuantity(),
            request.getMetadata()
        );
        return repository.save(event);
    }

    /**
     * Epic 1.1.4: Track purchases
     * POST /api/events/purchase
     */
    @PostMapping("/purchase")
    public RecommendationEvent purchase(@Valid @RequestBody PurchaseRequest request) {
        logger.info("Tracking PURCHASE: orderId={}, userId={}, sessionId={}",
            request.getOrderId(), request.getUserId(), request.getSessionId());

        RecommendationEvent event = RecommendationEvent.purchase(
            request.getOrderId(),
            request.getUserId(),
            request.getSessionId(),
            request.getMetadata()
        );
        return repository.save(event);
    }

    /**
     * Epic 1.1.5: Generic event tracking endpoint
     * POST /api/events/track
     */
    @PostMapping("/track")
    public RecommendationEvent trackEvent(@RequestBody RecommendationEvent event) {
        event.setTimestamp(Instant.now());
        logger.info("Tracking event: eventType={}, productId={}", event.getEventType(), event.getProductId());
        return repository.save(event);
    }

    /**
     * Epic 1.1.5: Batch event tracking
     * POST /api/events/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<List<RecommendationEvent>> trackEventsBatch(@RequestBody List<RecommendationEvent> events) {
        logger.info("Tracking batch of {} events", events.size());
        events.forEach(e -> {
            if (e.getTimestamp() == null) {
                e.setTimestamp(Instant.now());
            }
        });
        List<RecommendationEvent> saved = repository.saveAll(events);
        return ResponseEntity.ok(saved);
    }

    /**
     * Get all events (optional, for monitoring)
     * GET /api/events/all
     */
    @GetMapping("/all")
    public List<RecommendationEvent> getAllEvents() {
        logger.info("Fetching all events");
        return repository.findAll();
    }

    /**
     * Delete old events (cleanup)
     * DELETE /api/events/cleanup
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanupOldEvents() {
        logger.info("Cleaning up old events");
        // Delete events older than 90 days
        Instant ninetyDaysAgo = Instant.now().minusSeconds(90 * 24 * 60 * 60);
        repository.deleteAll();  // In production, use custom query
        return ResponseEntity.ok("Old events cleaned up");
    }
}
