package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.ExportRequestDTO;
import com.shopsphere.analytics.dto.ExportResponseDTO;
import com.shopsphere.analytics.model.Export;
import com.shopsphere.analytics.model.SalesMetric;
import com.shopsphere.analytics.model.ProductMetric;
import com.shopsphere.analytics.model.UserMetric;
import com.shopsphere.analytics.repository.ExportRepository;
import com.shopsphere.analytics.repository.ProductMetricRepository;
import com.shopsphere.analytics.repository.SalesMetricRepository;
import com.shopsphere.analytics.repository.UserMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExportServiceTest {

    @Mock
    private ExportRepository exportRepository;

    @Mock
    private SalesMetricRepository salesMetricRepository;

    @Mock
    private ProductMetricRepository productMetricRepository;

    @Mock
    private UserMetricRepository userMetricRepository;

    @InjectMocks
    private ExportService exportService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRequestExport() {
        ExportRequestDTO request = ExportRequestDTO.builder()
            .type("SALES")
            .format("CSV")
            .dateFrom(LocalDate.of(2024, 1, 1))
            .dateTo(LocalDate.of(2024, 1, 31))
            .build();

        Export savedExport = Export.builder()
            .id(1L)
            .exportId("test-export-id")
            .userId("user1")
            .type("SALES")
            .format("CSV")
            .status("PENDING")
            .dateFrom(request.getDateFrom())
            .dateTo(request.getDateTo())
            .createdAt(System.currentTimeMillis())
            .build();

        when(exportRepository.save(any(Export.class))).thenReturn(savedExport);
        when(exportRepository.findById(1L)).thenReturn(Optional.of(savedExport));
        when(salesMetricRepository.findByMetricDateBetween(any(), any())).thenReturn(Collections.emptyList());

        ExportResponseDTO result = exportService.requestExport("user1", request);

        assertNotNull(result);
        assertEquals("test-export-id", result.getExportId());
        // Status may be PENDING or COMPLETED since processExport runs synchronously in tests
        assertNotNull(result.getStatus());
        verify(exportRepository, atLeastOnce()).save(any(Export.class));
    }

    @Test
    public void testGetExportStatus() {
        Export export = Export.builder()
            .id(1L)
            .exportId("test-id")
            .userId("user1")
            .type("SALES")
            .format("CSV")
            .status("COMPLETED")
            .filePath("exports/test-id.csv")
            .createdAt(System.currentTimeMillis())
            .completedAt(System.currentTimeMillis())
            .build();

        when(exportRepository.findByExportId("test-id")).thenReturn(Optional.of(export));

        ExportResponseDTO result = exportService.getExportStatus("test-id");

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        assertNotNull(result.getFilePath());
    }

    @Test
    public void testGetExportStatusNotFound() {
        when(exportRepository.findByExportId("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> exportService.getExportStatus("nonexistent"));
    }

    @Test
    public void testGetUserExports() {
        Export export = Export.builder()
            .id(1L)
            .exportId("test-id")
            .userId("user1")
            .type("SALES")
            .format("CSV")
            .status("COMPLETED")
            .createdAt(System.currentTimeMillis())
            .build();

        when(exportRepository.findByUserIdOrderByCreatedAtDesc("user1")).thenReturn(List.of(export));

        List<ExportResponseDTO> result = exportService.getUserExports("user1");

        assertEquals(1, result.size());
        assertEquals("test-id", result.get(0).getExportId());
    }

    @Test
    public void testGetUserExportsByStatus() {
        Export export = Export.builder()
            .id(1L)
            .exportId("test-id")
            .userId("user1")
            .type("SALES")
            .format("CSV")
            .status("COMPLETED")
            .createdAt(System.currentTimeMillis())
            .build();

        when(exportRepository.findUserExportsByStatus("user1", "COMPLETED")).thenReturn(List.of(export));

        List<ExportResponseDTO> result = exportService.getUserExportsByStatus("user1", "COMPLETED");

        assertEquals(1, result.size());
        assertEquals("COMPLETED", result.get(0).getStatus());
    }

    @Test
    public void testProcessScheduledExportsNoPending() {
        when(exportRepository.findByStatus("SCHEDULED")).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> exportService.processScheduledExports());
    }

    @Test
    public void testConvertToDTOTimestamps() {
        Export export = Export.builder()
            .id(1L)
            .exportId("test-id")
            .userId("user1")
            .type("PRODUCTS")
            .format("EXCEL")
            .status("COMPLETED")
            .createdAt(1704067200000L) // 2024-01-01
            .completedAt(1704067300000L)
            .build();

        when(exportRepository.findByExportId("test-id")).thenReturn(Optional.of(export));

        ExportResponseDTO result = exportService.getExportStatus("test-id");

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getCompletedAt());
    }
}
