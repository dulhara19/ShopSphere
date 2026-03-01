package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.model.ABTest;
import com.shopsphere.analytics.service.ABTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/ab-tests")
@RequiredArgsConstructor
public class ABTestController {

    private final ABTestService abTestService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ABTest>> createTest(@RequestBody ABTest test) {
        ABTest created = abTestService.createTest(test);
        return ResponseEntity.ok(ApiResponseDTO.success(created, "A/B Test created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ABTest>>> getAllTests() {
        return ResponseEntity.ok(ApiResponseDTO.success(abTestService.getAllTests(), "Tests retrieved"));
    }

    @GetMapping("/{testId}")
    public ResponseEntity<ApiResponseDTO<ABTest>> getTest(@PathVariable Long testId) {
        return ResponseEntity.ok(ApiResponseDTO.success(abTestService.getTestById(testId), "Test retrieved"));
    }

    @PostMapping("/{testId}/start")
    public ResponseEntity<ApiResponseDTO<ABTest>> startTest(@PathVariable Long testId) {
        return ResponseEntity.ok(ApiResponseDTO.success(abTestService.startTest(testId), "Test started"));
    }

    @PutMapping("/{testId}/conclude")
    public ResponseEntity<ApiResponseDTO<ABTest>> concludeTest(@PathVariable Long testId) {
        return ResponseEntity.ok(ApiResponseDTO.success(abTestService.concludeTest(testId), "Test concluded"));
    }
}
