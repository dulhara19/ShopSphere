package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.dto.AddToCartRequest;
import com.shopsphere.recommendation.dto.ProductViewRequest;
import com.shopsphere.recommendation.dto.PurchaseRequest;
import com.shopsphere.recommendation.dto.SearchQueryRequest;
import com.shopsphere.recommendation.model.RecommendationEvent;
import com.shopsphere.recommendation.repository.EventTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/events")
public class EventTrackingController {

    private static final Logger logger = LoggerFactory.getLogger(EventTrackingController.class);

    @Autowired
    private EventTrackingRepository repository;

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

    // Optional generic endpoints remain unchanged
    @PostMapping("/track")
    public RecommendationEvent trackEvent(@RequestBody RecommendationEvent event) {
        event.setTimestamp(Instant.now());
        return repository.save(event);
    }

    @PostMapping("/track/batch")
    public List<RecommendationEvent> trackEventsBatch(@RequestBody List<RecommendationEvent> events) {
        events.forEach(e -> e.setTimestamp(Instant.now()));
        return repository.saveAll(events);
    }
}
