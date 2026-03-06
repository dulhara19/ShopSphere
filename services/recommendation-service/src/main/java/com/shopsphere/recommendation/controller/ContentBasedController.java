package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.model.ProductEmbedding;
import com.shopsphere.recommendation.service.ContentBasedService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations/content")
public class ContentBasedController {

    private final ContentBasedService contentService;

    public ContentBasedController(ContentBasedService contentService) {
        this.contentService = contentService;
    }

    @PostMapping("/embed")
    public ProductEmbedding generateEmbedding(@RequestParam String productId,
            @RequestParam String description) {
        return contentService.generateEmbedding(productId, description);
    }

    @GetMapping("/similar/{productId}")
    public List<String> similarContent(@PathVariable String productId,
            @RequestParam(defaultValue = "10") int limit) {
        return contentService.findSimilarByContent(productId, limit);
    }
}
