package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.service.RealTimePersonalizationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RealTimePersonalizationController {

    private final RealTimePersonalizationService rtService;

    public RealTimePersonalizationController(RealTimePersonalizationService rtService) {
        this.rtService = rtService;
    }

    @GetMapping("/real-time")
    public List<String> getRealTime(@RequestParam Map<String, Object> context,
                                    @RequestParam(defaultValue = "20") int limit) {
        return rtService.getRealTimeRecommendations(context, limit);
    }

    @GetMapping("/{id}/explanation")
    public String explain(@PathVariable("id") String recommendationId) {
        return rtService.explainRecommendation(recommendationId);
    }
}
