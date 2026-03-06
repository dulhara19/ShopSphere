package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.service.RecentlyViewedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecentlyViewedController {

    private final RecentlyViewedService recentlyViewedService;

    public RecentlyViewedController(RecentlyViewedService recentlyViewedService) {
        this.recentlyViewedService = recentlyViewedService;
    }

    /**
     * Epic 1.2.2: Get recently viewed products
     * GET /api/recommendations/recently-viewed
     */
    @GetMapping("/recently-viewed")
    public List<String> getRecentlyViewed(
        @RequestParam String sessionId,
        @RequestParam(defaultValue = "10") int limit
    ) {
        return recentlyViewedService.getRecentlyViewed(sessionId, limit);
    }

    /**
     * Add a product to recently viewed
     * POST /api/recommendations/recently-viewed
     */
    @PostMapping("/recently-viewed")
    public ResponseEntity<String> addRecentlyViewed(
        @RequestParam String sessionId,
        @RequestParam String productId
    ) {
        recentlyViewedService.addRecentlyViewed(sessionId, productId);
        return ResponseEntity.ok("Product added to recently viewed");
    }

    /**
     * Epic 1.2.3: Clear recently viewed history
     * DELETE /api/recommendations/recently-viewed
     */
    @DeleteMapping("/recently-viewed")
    public ResponseEntity<String> clearRecentlyViewed(
        @RequestParam String sessionId
    ) {
        recentlyViewedService.clearRecentlyViewed(sessionId);
        return ResponseEntity.ok("Recently viewed history cleared");
    }

    /**
     * Epic 1.2.4: Merge session history with user account (on login)
     * POST /api/recommendations/recently-viewed/merge
     */
    @PostMapping("/recently-viewed/merge")
    public ResponseEntity<String> mergeSessionToUser(
        @RequestParam String userId,
        @RequestParam String sessionId
    ) {
        recentlyViewedService.mergeSessionToUser(userId, sessionId);
        return ResponseEntity.ok("Session history merged with user account");
    }
}
