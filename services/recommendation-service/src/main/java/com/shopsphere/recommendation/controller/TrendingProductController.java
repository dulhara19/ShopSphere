package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.model.TrendingProduct;
import com.shopsphere.recommendation.service.TrendingProductService;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Epic 1.3: Trending Products Controller
 */
@RestController
@RequestMapping("/api/recommendations/trending")
public class TrendingProductController {

    private static final Logger logger = LoggerFactory.getLogger(TrendingProductController.class);

    private final TrendingProductService trendingProductService;

    public TrendingProductController(TrendingProductService trendingProductService) {
        this.trendingProductService = trendingProductService;
    }

    /**
     * Epic 1.3.2: Get global trending products
     * GET /api/recommendations/trending?limit=20
     */
    @GetMapping
    public List<TrendingProduct> getGlobalTrendingProducts(
        @RequestParam(defaultValue = "20") int limit
    ) {
        logger.info("Fetching {} global trending products", limit);
        return trendingProductService.getGlobalTrendingProducts(limit);
    }

    /**
     * Epic 1.3.3: Get category trending products
     * GET /api/recommendations/trending/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public List<TrendingProduct> getCategoryTrendingProducts(
        @PathVariable String categoryId,
        @RequestParam(defaultValue = "20") int limit
    ) {
        logger.info("Fetching {} trending products for category {}", limit, categoryId);
        return trendingProductService.getCategoryTrendingProducts(categoryId, limit);
    }
}
