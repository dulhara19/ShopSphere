package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.model.CustomReport;
import com.shopsphere.analytics.service.CustomReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics/reports")
@RequiredArgsConstructor
public class CustomReportController {

    private final CustomReportService customReportService;

    @PostMapping("/custom")
    public ResponseEntity<ApiResponseDTO<CustomReport>> createReport(@RequestBody CustomReport report) {
        CustomReport saved = customReportService.saveReport(report);
        return ResponseEntity.ok(ApiResponseDTO.success(saved, "Custom report saved"));
    }

    @GetMapping("/saved")
    public ResponseEntity<ApiResponseDTO<List<CustomReport>>> getSavedReports(
            @RequestHeader(value = "X-User-Id", defaultValue = "admin") String userId) {
        return ResponseEntity
                .ok(ApiResponseDTO.success(customReportService.getUserReports(userId), "Saved reports retrieved"));
    }

    @GetMapping("/{reportId}/run")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> runReport(@PathVariable Long reportId) {
        Map<String, Object> result = customReportService.runReport(reportId);
        return ResponseEntity.ok(ApiResponseDTO.success(result, "Report execution completed"));
    }

    @PostMapping("/{reportId}/schedule")
    public ResponseEntity<ApiResponseDTO<CustomReport>> scheduleReport(
            @PathVariable Long reportId,
            @RequestParam String cron,
            @RequestParam String emails) {

        CustomReport report = customReportService.scheduleReport(reportId, cron, emails);
        return ResponseEntity.ok(ApiResponseDTO.success(report, "Report scheduled"));
    }
}
