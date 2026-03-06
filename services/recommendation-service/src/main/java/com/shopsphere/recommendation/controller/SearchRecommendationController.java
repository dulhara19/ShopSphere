package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.service.SearchRecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations/search")
public class SearchRecommendationController {

    private final SearchRecommendationService searchService;

    public SearchRecommendationController(SearchRecommendationService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam String q,
                                     @RequestParam(defaultValue = "10") int limit) {
        return searchService.autocomplete(q, limit);
    }

    @GetMapping("/alternatives")
    public List<String> alternatives(@RequestParam String q,
                                     @RequestParam(defaultValue = "5") int limit) {
        // stub for did you mean
        return List.of();
    }
}
