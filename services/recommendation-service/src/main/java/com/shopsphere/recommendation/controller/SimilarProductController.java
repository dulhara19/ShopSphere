package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.model.SimilarProduct;
import com.shopsphere.recommendation.service.SimilarProductService;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Epic 1.4: Similar Products Controller
 */
@RestController
@RequestMapping("/api/recommendations/similar")
public class SimilarProductController {

    private static final Logger logger = LoggerFactory.getLogger(SimilarProductController.class);

    private final SimilarProductService similarProductService;

    public SimilarProductController(SimilarProductService similarProductService) {
        this.similarProductService = similarProductService;
    }

    /**
     * Epic 1.4.3: Get similar products for a product
     * GET /api/recommendations/similar/{productId}?limit=8
     */
    @GetMapping("/{productId}")
    public List<SimilarProduct> getSimilarProducts(
        @PathVariable String productId,
        @RequestParam(defaultValue = "8") int limit
    ) {
        logger.info("Fetching {} similar products for productId={}", limit, productId);
        return similarProductService.getSimilarProducts(productId, limit);
    }
}
