package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.response.AnalyticsTurnoverResponse;
import com.shopsphere.inventory.dto.response.DaysRemainingResponse;
import com.shopsphere.inventory.dto.response.DeadStockResponse;
import com.shopsphere.inventory.dto.response.RestockRecommendationResponse;
import com.shopsphere.inventory.dto.response.ValuationResponse;
import com.shopsphere.inventory.service.InventoryAnalyticsService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryAnalyticsController {

    private final InventoryAnalyticsService inventoryAnalyticsService;

    @GetMapping("/analytics/turnover")
    public ResponseEntity<List<AnalyticsTurnoverResponse>> getTurnover() {
        return ResponseEntity.ok(inventoryAnalyticsService.getTurnover());
    }

    @GetMapping("/analytics/days-remaining")
    public ResponseEntity<List<DaysRemainingResponse>> getDaysRemaining() {
        return ResponseEntity.ok(inventoryAnalyticsService.getDaysRemaining());
    }

    @GetMapping("/analytics/dead-stock")
    public ResponseEntity<List<DeadStockResponse>> getDeadStock(
            @RequestParam(required = false) Integer thresholdDays) {
        return ResponseEntity.ok(inventoryAnalyticsService.getDeadStock(thresholdDays));
    }

    @GetMapping("/analytics/valuation")
    public ResponseEntity<List<ValuationResponse>> getValuation(
            @RequestParam(required = false) Double unitCost) {
        return ResponseEntity.ok(inventoryAnalyticsService.getValuation(unitCost));
    }

    @GetMapping("/{productId}/restock-recommendation")
    public ResponseEntity<RestockRecommendationResponse> getRestockRecommendation(@PathVariable UUID productId) {
        return ResponseEntity.ok(inventoryAnalyticsService.getRestockRecommendation(productId));
    }

    @GetMapping("/forecasts")
    public ResponseEntity<List<RestockRecommendationResponse>> getForecasts() {
        return ResponseEntity.ok(inventoryAnalyticsService.getForecasts());
    }
}
