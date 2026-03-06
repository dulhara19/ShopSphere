package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.service.CollaborativeFilteringService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations/cf")
public class CollaborativeFilteringController {

    private final CollaborativeFilteringService cfService;

    public CollaborativeFilteringController(CollaborativeFilteringService cfService) {
        this.cfService = cfService;
    }

    @GetMapping("/users-like-you")
    public List<String> usersLikeYou(@RequestParam String userId,
                                     @RequestParam(defaultValue = "10") int limit) {
        return cfService.recommendUsersLikeYou(userId, limit);
    }

    @GetMapping("/because-you-bought/{productId}")
    public List<String> becauseYouBought(@PathVariable String productId,
                                         @RequestParam(defaultValue = "10") int limit) {
        return cfService.recommendBecauseYouBought(productId, limit);
    }
}
