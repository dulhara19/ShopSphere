package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.dto.FunnelDTO;
import com.shopsphere.analytics.dto.SalesMetricDTO;
import com.shopsphere.analytics.dto.SalesSummaryDTO;
import com.shopsphere.analytics.service.SalesAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics/sales")
@RequiredArgsConstructor
public class SalesAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponseDTO<SalesSummaryDTO>> getSalesSummary(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        SalesSummaryDTO summary = salesAnalyticsService.getSalesSummary(fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(summary, "Sales summary retrieved"));
    }

    @GetMapping("/by-date")
    public ResponseEntity<ApiResponseDTO<List<SalesMetricDTO>>> getSalesByDate(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to,
        @RequestParam(name = "granularity", defaultValue = "day") String granularity) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        List<SalesMetricDTO> metrics = salesAnalyticsService.getSalesByDateRange(fromDate, toDate, granularity);
        return ResponseEntity.ok(ApiResponseDTO.success(metrics, "Sales by date retrieved"));
    }

    @GetMapping("/by-category")
    public ResponseEntity<ApiResponseDTO<List<SalesMetricDTO>>> getSalesByCategory(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        List<SalesMetricDTO> metrics = salesAnalyticsService.getSalesByCategory(fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(metrics, "Sales by category retrieved"));
    }

    @GetMapping("/by-product")
    public ResponseEntity<ApiResponseDTO<List<SalesMetricDTO>>> getTopProducts(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to,
        @RequestParam(name = "limit", defaultValue = "10") int limit) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        List<SalesMetricDTO> metrics = salesAnalyticsService.getTopProducts(limit, fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(metrics, "Top products retrieved"));
    }

    @GetMapping("/top-categories")
    public ResponseEntity<ApiResponseDTO<List<SalesMetricDTO>>> getTopCategories(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to,
        @RequestParam(name = "limit", defaultValue = "10") int limit) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        List<SalesMetricDTO> metrics = salesAnalyticsService.getTopCategories(limit, fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(metrics, "Top categories retrieved"));
    }

    @GetMapping("/funnel")
    public ResponseEntity<ApiResponseDTO<FunnelDTO>> getConversionFunnel(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        FunnelDTO funnel = salesAnalyticsService.getConversionFunnel(fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(funnel, "Funnel retrieved"));
    }
}
