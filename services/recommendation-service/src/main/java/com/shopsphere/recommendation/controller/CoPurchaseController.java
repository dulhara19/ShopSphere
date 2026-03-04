package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.model.CoPurchaseMatrix;
import com.shopsphere.recommendation.service.CoPurchaseService;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Epic 1.5: Customers Also Bought Controller
 */
@RestController
@RequestMapping("/api/recommendations")
public class CoPurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(CoPurchaseController.class);

    private final CoPurchaseService coPurchaseService;

    public CoPurchaseController(CoPurchaseService coPurchaseService) {
        this.coPurchaseService = coPurchaseService;
    }

    /**
     * Epic 1.5.2: Get products frequently bought together
     * GET /api/recommendations/also-bought/{productId}?limit=5
     */
    @GetMapping("/also-bought/{productId}")
    public List<CoPurchaseMatrix> getAlsoBoughtProducts(
        @PathVariable String productId,
        @RequestParam(defaultValue = "5") int limit
    ) {
        logger.info("Fetching {} 'also-bought' products for productId={}", limit, productId);
        return coPurchaseService.getAlsoBoughtProducts(productId, limit);
    }

    /**
     * Epic 1.5.3: Get bundle suggestions
     * GET /api/recommendations/bundle/{productId}
     */
    @GetMapping("/bundle/{productId}")
    public List<CoPurchaseMatrix> getBundleSuggestions(
        @PathVariable String productId,
        @RequestParam(defaultValue = "3") int bundleSize
    ) {
        logger.info("Fetching bundle suggestions for productId={}, bundleSize={}", productId, bundleSize);
        return coPurchaseService.getBundleSuggestions(productId, bundleSize);
    }
}
