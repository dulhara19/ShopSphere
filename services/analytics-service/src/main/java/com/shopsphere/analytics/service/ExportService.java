package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.ExportRequestDTO;
import com.shopsphere.analytics.dto.ExportResponseDTO;
import com.shopsphere.analytics.model.Export;
import com.shopsphere.analytics.repository.ExportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final ExportRepository exportRepository;

    @Transactional
    public ExportResponseDTO requestExport(String userId, ExportRequestDTO request) {
        String exportId = UUID.randomUUID().toString();

        Export export = Export.builder()
            .exportId(exportId)
            .userId(userId)
            .type(request.getType())
            .format(request.getFormat())
            .status("PENDING")
            .dateFrom(request.getDateFrom())
            .dateTo(request.getDateTo())
            .filters(request.getFilters() != null ? request.getFilters().toString() : null)
            .build();

        Export saved = exportRepository.save(export);
        
        // Process export asynchronously
        processExport(saved.getId());

        return convertToDTO(saved);
    }

    @Async
    @Transactional
    public void processExport(Long exportId) {
        try {
            Export export = exportRepository.findById(exportId).orElseThrow();
            export.setStatus("PROCESSING");
            exportRepository.save(export);

            log.info("Processing export: {} of type: {}", export.getExportId(), export.getType());

            // Simulate export processing
            Thread.sleep(2000);

            String filePath = "/exports/" + export.getExportId() + "." + export.getFormat().toLowerCase();

            export.setStatus("COMPLETED");
            export.setFilePath(filePath);
            export.setCompletedAt(System.currentTimeMillis());
            exportRepository.save(export);

            log.info("Export completed: {}", export.getExportId());
        } catch (Exception e) {
            log.error("Error processing export: {}", exportId, e);
            Export export = exportRepository.findById(exportId).orElseThrow();
            export.setStatus("FAILED");
            export.setErrorMessage(e.getMessage());
            exportRepository.save(export);
        }
    }

    public ExportResponseDTO getExportStatus(String exportId) {
        Export export = exportRepository.findByExportId(exportId)
            .orElseThrow(() -> new RuntimeException("Export not found"));
        return convertToDTO(export);
    }

    public List<ExportResponseDTO> getUserExports(String userId) {
        List<Export> exports = exportRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return exports.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<ExportResponseDTO> getUserExportsByStatus(String userId, String status) {
        List<Export> exports = exportRepository.findUserExportsByStatus(userId, status);
        return exports.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private ExportResponseDTO convertToDTO(Export export) {
        return ExportResponseDTO.builder()
            .exportId(export.getExportId())
            .status(export.getStatus())
            .type(export.getType())
            .format(export.getFormat())
            .filePath(export.getFilePath())
            .createdAt(LocalDateTime.now().minusDays(0)) // Placeholder
            .completedAt(export.getCompletedAt() != null ? LocalDateTime.now() : null)
            .errorMessage(export.getErrorMessage())
            .build();
    }
}
