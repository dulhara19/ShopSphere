package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.dto.DashboardDTO;
import com.shopsphere.analytics.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    public ResponseEntity<ApiResponseDTO<DashboardDTO>> getAdminDashboard() {
        DashboardDTO dashboard = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(ApiResponseDTO.success(dashboard, "Admin dashboard retrieved"));
    }

    @GetMapping("/seller")
    public ResponseEntity<ApiResponseDTO<DashboardDTO>> getSellerDashboard(
        @RequestParam(name = "sellerId") String sellerId) {
        
        DashboardDTO dashboard = dashboardService.getSellerDashboard(sellerId);
        return ResponseEntity.ok(ApiResponseDTO.success(dashboard, "Seller dashboard retrieved"));
    }

    @PostMapping("/invalidate-cache")
    public ResponseEntity<ApiResponseDTO<String>> invalidateCache() {
        dashboardService.invalidateDashboardCache();
        return ResponseEntity.ok(ApiResponseDTO.success("Cache invalidated", "Dashboard cache cleared"));
    }
}
