package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.dto.ProductMetricDTO;
import com.shopsphere.analytics.service.ProductAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics/products")
@RequiredArgsConstructor
public class ProductAnalyticsController {

    private final ProductAnalyticsService productAnalyticsService;

    @GetMapping("/{productId}/performance")
    public ResponseEntity<ApiResponseDTO<ProductMetricDTO>> getProductPerformance(
        @PathVariable String productId,
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        ProductMetricDTO metrics = productAnalyticsService.getProductPerformance(productId, fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(metrics, "Product performance retrieved"));
    }

    @GetMapping("/top-viewed")
    public ResponseEntity<ApiResponseDTO<List<ProductMetricDTO>>> getTopViewedProducts(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to,
        @RequestParam(name = "limit", defaultValue = "10") int limit) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        List<ProductMetricDTO> metrics = productAnalyticsService.getTopViewedProducts(limit, fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(metrics, "Top viewed products retrieved"));
    }

    @GetMapping("/top-selling")
    public ResponseEntity<ApiResponseDTO<List<ProductMetricDTO>>> getTopSellingProducts(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to,
        @RequestParam(name = "limit", defaultValue = "10") int limit) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        List<ProductMetricDTO> metrics = productAnalyticsService.getTopSellingProducts(limit, fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(metrics, "Top selling products retrieved"));
    }

    @GetMapping("/top-revenue")
    public ResponseEntity<ApiResponseDTO<List<ProductMetricDTO>>> getTopRevenueProducts(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to,
        @RequestParam(name = "limit", defaultValue = "10") int limit) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        List<ProductMetricDTO> metrics = productAnalyticsService.getTopRevenueProducts(limit, fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(metrics, "Top revenue products retrieved"));
    }

    @GetMapping("/categories/{categoryId}/performance")
    public ResponseEntity<ApiResponseDTO<List<ProductMetricDTO>>> getCategoryPerformance(
        @PathVariable String categoryId,
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        List<ProductMetricDTO> metrics = productAnalyticsService.getCategoryPerformance(categoryId, fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(metrics, "Category performance retrieved"));
    }

}
