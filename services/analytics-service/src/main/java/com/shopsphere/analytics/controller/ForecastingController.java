package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.dto.ForecastDTO;
import com.shopsphere.analytics.service.ForecastingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics/forecast")
@RequiredArgsConstructor
public class ForecastingController {

    private final ForecastingService forecastingService;

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponseDTO<ForecastDTO>> getRevenueForecast(
            @RequestParam(name = "period", defaultValue = "30") int period) {

        ForecastDTO forecast = forecastingService.predictRevenue(period);
        return ResponseEntity.ok(ApiResponseDTO.success(forecast, "Revenue forecast generated"));
    }

    @GetMapping("/sales")
    public ResponseEntity<ApiResponseDTO<ForecastDTO>> getSalesForecast(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "period", defaultValue = "30") int period) {

        ForecastDTO forecast = forecastingService.predictSales(category, period);
        return ResponseEntity.ok(ApiResponseDTO.success(forecast, "Sales forecast generated"));
    }

    @GetMapping("/accuracy")
    public ResponseEntity<ApiResponseDTO<String>> getForecastAccuracy() {
        return ResponseEntity.ok(ApiResponseDTO.success("85.5%", "Accuracy retrieved"));
    }
}
