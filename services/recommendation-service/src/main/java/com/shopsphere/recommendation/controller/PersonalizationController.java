package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.service.PersonalizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Epic 1.6: Basic Personalization Controller
 */
@RestController
@RequestMapping("/api/recommendations")
public class PersonalizationController {

    private static final Logger logger = LoggerFactory.getLogger(PersonalizationController.class);

    private final PersonalizationService personalizationService;

    public PersonalizationController(PersonalizationService personalizationService) {
        this.personalizationService = personalizationService;
    }

    /**
     * Epic 1.6.3: Get homepage recommendations
     * GET /api/recommendations/homepage?userId=xxx
     */
    @GetMapping("/homepage")
    public List<String> getHomepageRecommendations(
        @RequestParam(required = false) String userId,
        @RequestParam(defaultValue = "20") int limit
    ) {
        logger.info("Fetching homepage recommendations for userId={}", userId);
        return personalizationService.getHomepageRecommendations(userId, limit);
    }

    /**
     * Epic 1.6.4: Get "For You" section recommendations
     * GET /api/recommendations/for-you?userId=xxx&limit=20
     */
    @GetMapping("/for-you")
    public List<String> getForYouRecommendations(
        @RequestParam String userId,
        @RequestParam(defaultValue = "20") int limit
    ) {
        logger.info("Fetching 'For You' recommendations for userId={}", userId);
        return personalizationService.getForYouRecommendations(userId, limit);
    }

    /**
     * Track a product view for personalization
     * POST /api/recommendations/track-view
     */
    @PostMapping("/track-view")
    public ResponseEntity<String> trackProductView(
        @RequestParam String userId,
        @RequestParam(required = false) String categoryId
    ) {
        personalizationService.trackProductView(userId, categoryId);
        logger.info("Tracked product view for userId={}, categoryId={}", userId, categoryId);
        return ResponseEntity.ok("Product view tracked");
    }

    /**
     * Track a purchase for personalization
     * POST /api/recommendations/track-purchase
     */
    @PostMapping("/track-purchase")
    public ResponseEntity<String> trackPurchase(
        @RequestParam String userId,
        @RequestParam(required = false) Integer priceAmount
    ) {
        personalizationService.trackPurchase(userId, priceAmount);
        logger.info("Tracked purchase for userId={}, price={}", userId, priceAmount);
        return ResponseEntity.ok("Purchase tracked");
    }

    /**
     * Get user's preferred categories
     * GET /api/recommendations/user-preferences/categories?userId=xxx
     */
    @GetMapping("/user-preferences/categories")
    public List<String> getUserPreferredCategories(
        @RequestParam String userId,
        @RequestParam(defaultValue = "5") int limit
    ) {
        logger.info("Fetching preferred categories for userId={}", userId);
        return personalizationService.getPreferredCategories(userId, limit);
    }

    /**
     * Get user's price range preference
     * GET /api/recommendations/user-preferences/price-range?userId=xxx
     */
    @GetMapping("/user-preferences/price-range")
    public Map<String, Integer> getUserPriceRange(
        @RequestParam String userId
    ) {
        logger.info("Fetching price range preference for userId={}", userId);
        return personalizationService.getUserPriceRange(userId);
    }

    /**
     * Merge session preferences on login
     * POST /api/recommendations/merge-session
     */
    @PostMapping("/merge-session")
    public ResponseEntity<String> mergeSessionPreferences(
        @RequestParam String userId,
        @RequestParam String sessionId
    ) {
        personalizationService.mergeSessionPreferences(userId, sessionId);
        logger.info("Merged session {} preferences for userId={}", sessionId, userId);
        return ResponseEntity.ok("Session preferences merged");
    }
}
