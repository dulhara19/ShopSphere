package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.service.SellerAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics/sellers")
@RequiredArgsConstructor
public class SellerAnalyticsController {

    private final SellerAnalyticsService sellerAnalyticsService;

    @GetMapping("/{sellerId}/metrics")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getSellerPerformance(
            @PathVariable String sellerId,
            @RequestParam(name = "from") String from,
            @RequestParam(name = "to") String to) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        return ResponseEntity.ok(ApiResponseDTO.success(
                sellerAnalyticsService.getSellerPerformance(sellerId, fromDate, toDate),
                "Seller performance retrieved"));
    }

    @GetMapping("/ranking")
    public ResponseEntity<ApiResponseDTO<List<Map<String, Object>>>> getSellerRanking(
            @RequestParam(name = "date", required = false) String date) {

        LocalDate queryDate = date != null ? LocalDate.parse(date) : LocalDate.now().minusDays(1);

        return ResponseEntity.ok(ApiResponseDTO.success(
                sellerAnalyticsService.getSellerRanking(queryDate),
                "Seller ranking retrieved"));
    }

    @GetMapping("/{sellerId}/commission")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getSellerCommission(
            @PathVariable String sellerId,
            @RequestParam(name = "from") String from,
            @RequestParam(name = "to") String to) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        return ResponseEntity.ok(ApiResponseDTO.success(
                sellerAnalyticsService.getCommissionReport(sellerId, fromDate, toDate),
                "Commission report retrieved"));
    }
}
