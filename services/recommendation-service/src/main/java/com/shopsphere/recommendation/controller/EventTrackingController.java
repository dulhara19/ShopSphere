package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.model.RecommendationEvent;
import com.shopsphere.recommendation.repository.EventTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.time.Instant;


@RestController
@RequestMapping("/api/events")
public class EventTrackingController {

    @Autowired
    private EventTrackingRepository repository;

    @PostMapping("/product-view")
    public RecommendationEvent productView(@RequestBody Map<String, Object> payload) {
        RecommendationEvent event = RecommendationEvent.productView(
            (String) payload.get("productId"),
            (String) payload.get("userId"),
            (String) payload.get("sessionId"),
            (Map<String, Object>) payload.get("metadata")
        );
        return repository.save(event);
    }

    @PostMapping("/search-query")
    public RecommendationEvent searchQuery(@RequestBody Map<String, Object> payload) {
        RecommendationEvent event = RecommendationEvent.searchQuery(
            (String) payload.get("userId"),
            (String) payload.get("sessionId"),
            (String) payload.get("searchTerm"),
            (Map<String, Object>) payload.get("metadata")
        );
        return repository.save(event);
    }

    @PostMapping("/add-to-cart")
    public RecommendationEvent addToCart(@RequestBody Map<String, Object> payload) {
        Map<String, Object> metadata = (Map<String, Object>) payload.get("metadata");
        Integer quantity = metadata != null ? (Integer) metadata.get("quantity") : null;

        RecommendationEvent event = RecommendationEvent.addToCart(
            (String) payload.get("productId"),
            (String) payload.get("userId"),
            (String) payload.get("sessionId"),
            quantity,
            null
        );
        return repository.save(event);
    }

    //1.1.4

    @PostMapping("/purchase")
    public RecommendationEvent purchase(@RequestBody Map<String, Object> payload) {
        RecommendationEvent event = RecommendationEvent.purchase(
            (String) payload.get("orderId"),
            (String) payload.get("userId"),
            (String) payload.get("sessionId"),
            (Map<String, Object>) payload.get("metadata")
        );
        return repository.save(event);
    }

    //1.1.5 Event Ingestion API

    @PostMapping("/track")
    public RecommendationEvent trackEvent(@RequestBody RecommendationEvent event) {
        // Save with current timestamp
        event.setTimestamp(Instant.now());
        return repository.save(event);
    }
    @PostMapping("/track/batch")
    public List<RecommendationEvent> trackEventsBatch(@RequestBody List<RecommendationEvent> events) {
        // Set timestamp for each event
        events.forEach(e -> e.setTimestamp(Instant.now()));
        return repository.saveAll(events);
    }

}
