package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.dto.ExportRequestDTO;
import com.shopsphere.analytics.dto.ExportResponseDTO;
import com.shopsphere.analytics.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping({"/api/analytics/export", "/api/analytics/exports"})
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ExportResponseDTO>> requestExport(
        @Valid @RequestBody ExportRequestDTO request,
        @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        // In production, extract userId from JWT token
        if (userId == null) {
            userId = "anonymous";
        }
        
        ExportResponseDTO response = exportService.requestExport(userId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(ApiResponseDTO.success(response, "Export request accepted"));
    }

    @GetMapping("/{exportId}")
    public ResponseEntity<ApiResponseDTO<ExportResponseDTO>> getExportStatus(
        @PathVariable String exportId) {
        
        ExportResponseDTO response = exportService.getExportStatus(exportId);
        return ResponseEntity.ok(ApiResponseDTO.success(response, "Export status retrieved"));
    }

    @GetMapping("/{exportId}/download")
    public ResponseEntity<ApiResponseDTO<String>> downloadExport(
        @PathVariable String exportId) {
        
        ExportResponseDTO export = exportService.getExportStatus(exportId);
        
        if (!"COMPLETED".equals(export.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("INVALID_STATUS", "Export is not yet completed"));
        }
        
        return ResponseEntity.ok(ApiResponseDTO.success(export.getDownloadUrl(), "Download URL provided"));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ExportResponseDTO>>> getUserExports(
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestParam(name = "status", required = false) String status) {
        
        if (userId == null) {
            userId = "anonymous";
        }
        
        List<ExportResponseDTO> exports;
        if (status != null) {
            exports = exportService.getUserExportsByStatus(userId, status);
        } else {
            exports = exportService.getUserExports(userId);
        }
        
        return ResponseEntity.ok(ApiResponseDTO.success(exports, "User exports retrieved"));
    }
}
