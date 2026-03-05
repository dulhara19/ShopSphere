package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.ExportRequestDTO;
import com.shopsphere.analytics.dto.ExportResponseDTO;
import com.shopsphere.analytics.model.Export;
import com.shopsphere.analytics.model.ProductMetric;
import com.shopsphere.analytics.model.SalesMetric;
import com.shopsphere.analytics.model.UserMetric;
import com.shopsphere.analytics.repository.ExportRepository;
import com.shopsphere.analytics.repository.ProductMetricRepository;
import com.shopsphere.analytics.repository.SalesMetricRepository;
import com.shopsphere.analytics.repository.UserMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final ExportRepository exportRepository;
    private final SalesMetricRepository salesMetricRepository;
    private final ProductMetricRepository productMetricRepository;
    private final UserMetricRepository userMetricRepository;

    private static final String EXPORTS_DIR = "exports";

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

            File dir = new File(EXPORTS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = export.getExportId() + "." + export.getFormat().toLowerCase();
            if ("EXCEL".equalsIgnoreCase(export.getFormat())) {
                fileName = export.getExportId() + ".xlsx";
            }
            String filePath = EXPORTS_DIR + File.separator + fileName;

            switch (export.getFormat().toUpperCase()) {
                case "CSV":
                    generateCsv(export, filePath);
                    break;
                case "EXCEL":
                    generateExcel(export, filePath);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + export.getFormat());
            }

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

    private void generateCsv(Export export, String filePath) throws IOException {
        switch (export.getType().toUpperCase()) {
            case "SALES":
                generateSalesCsv(export, filePath);
                break;
            case "PRODUCTS":
                generateProductsCsv(export, filePath);
                break;
            case "USERS":
                generateUsersCsv(export, filePath);
                break;
            default:
                generateSalesCsv(export, filePath);
        }
    }

    private void generateSalesCsv(Export export, String filePath) throws IOException {
        List<SalesMetric> metrics = salesMetricRepository.findByMetricDateBetween(
            export.getDateFrom(), export.getDateTo());

        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                 .withHeader("Date", "Granularity", "Category", "Product", "Revenue", "Orders", "Items", "AOV", "Views", "Add to Cart", "Checkouts", "Purchases", "Seller"))) {
            for (SalesMetric m : metrics) {
                printer.printRecord(
                    m.getMetricDate(), m.getGranularity(), m.getCategoryId(), m.getProductId(),
                    m.getTotalRevenue(), m.getTotalOrders(), m.getTotalItems(), m.getAverageOrderValue(),
                    m.getViewCount(), m.getAddToCartCount(), m.getCheckoutCount(), m.getPurchaseCount(), m.getSeller());
            }
        }
    }

    private void generateProductsCsv(Export export, String filePath) throws IOException {
        List<ProductMetric> metrics = productMetricRepository.findByMetricDateBetween(
            export.getDateFrom(), export.getDateTo());

        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                 .withHeader("Date", "Product ID", "Category ID", "Views", "Unique Viewers", "Add to Cart", "Purchases", "Revenue", "Units Sold", "Conversion Rate", "Avg Rating", "Reviews"))) {
            for (ProductMetric m : metrics) {
                printer.printRecord(
                    m.getMetricDate(), m.getProductId(), m.getCategoryId(),
                    m.getViewCount(), m.getUniqueViewers(), m.getAddToCartCount(), m.getPurchaseCount(),
                    m.getRevenue(), m.getUnitsSold(), m.getConversionRate(), m.getAvgRating(), m.getReviewCount());
            }
        }
    }

    private void generateUsersCsv(Export export, String filePath) throws IOException {
        List<UserMetric> metrics = userMetricRepository.findByMetricDateBetween(
            export.getDateFrom(), export.getDateTo());

        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                 .withHeader("Date", "User ID", "Total Purchases", "Lifetime Value", "Session Count", "Page Views", "Active", "Segment", "Activity Level"))) {
            for (UserMetric m : metrics) {
                printer.printRecord(
                    m.getMetricDate(), m.getUserId(), m.getTotalPurchases(), m.getLifetimeValue(),
                    m.getSessionCount(), m.getPageViewCount(), m.getActive(), m.getSegment(), m.getActivityLevel());
            }
        }
    }

    private void generateExcel(Export export, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            switch (export.getType().toUpperCase()) {
                case "SALES":
                    generateSalesExcel(export, workbook);
                    break;
                case "PRODUCTS":
                    generateProductsExcel(export, workbook);
                    break;
                case "USERS":
                    generateUsersExcel(export, workbook);
                    break;
                default:
                    generateSalesExcel(export, workbook);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    private void generateSalesExcel(Export export, Workbook workbook) {
        Sheet sheet = workbook.createSheet("Sales");
        String[] headers = {"Date", "Granularity", "Category", "Product", "Revenue", "Orders", "Items", "AOV", "Views", "Add to Cart", "Checkouts", "Purchases", "Seller"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        List<SalesMetric> metrics = salesMetricRepository.findByMetricDateBetween(
            export.getDateFrom(), export.getDateTo());

        int rowNum = 1;
        for (SalesMetric m : metrics) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getMetricDate().toString());
            row.createCell(1).setCellValue(m.getGranularity());
            row.createCell(2).setCellValue(m.getCategoryId() != null ? m.getCategoryId() : "");
            row.createCell(3).setCellValue(m.getProductId() != null ? m.getProductId() : "");
            row.createCell(4).setCellValue(m.getTotalRevenue().doubleValue());
            row.createCell(5).setCellValue(m.getTotalOrders());
            row.createCell(6).setCellValue(m.getTotalItems());
            row.createCell(7).setCellValue(m.getAverageOrderValue().doubleValue());
            row.createCell(8).setCellValue(m.getViewCount());
            row.createCell(9).setCellValue(m.getAddToCartCount());
            row.createCell(10).setCellValue(m.getCheckoutCount());
            row.createCell(11).setCellValue(m.getPurchaseCount());
            row.createCell(12).setCellValue(m.getSeller() != null ? m.getSeller() : "");
        }
    }

    private void generateProductsExcel(Export export, Workbook workbook) {
        Sheet sheet = workbook.createSheet("Products");
        String[] headers = {"Date", "Product ID", "Category ID", "Views", "Unique Viewers", "Add to Cart", "Purchases", "Revenue", "Units Sold", "Conversion Rate", "Avg Rating", "Reviews"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        List<ProductMetric> metrics = productMetricRepository.findByMetricDateBetween(
            export.getDateFrom(), export.getDateTo());

        int rowNum = 1;
        for (ProductMetric m : metrics) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getMetricDate().toString());
            row.createCell(1).setCellValue(m.getProductId());
            row.createCell(2).setCellValue(m.getCategoryId());
            row.createCell(3).setCellValue(m.getViewCount());
            row.createCell(4).setCellValue(m.getUniqueViewers());
            row.createCell(5).setCellValue(m.getAddToCartCount());
            row.createCell(6).setCellValue(m.getPurchaseCount());
            row.createCell(7).setCellValue(m.getRevenue().doubleValue());
            row.createCell(8).setCellValue(m.getUnitsSold());
            row.createCell(9).setCellValue(m.getConversionRate().doubleValue());
            row.createCell(10).setCellValue(m.getAvgRating().doubleValue());
            row.createCell(11).setCellValue(m.getReviewCount());
        }
    }

    private void generateUsersExcel(Export export, Workbook workbook) {
        Sheet sheet = workbook.createSheet("Users");
        String[] headers = {"Date", "User ID", "Total Purchases", "Lifetime Value", "Session Count", "Page Views", "Active", "Segment", "Activity Level"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        List<UserMetric> metrics = userMetricRepository.findByMetricDateBetween(
            export.getDateFrom(), export.getDateTo());

        int rowNum = 1;
        for (UserMetric m : metrics) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getMetricDate().toString());
            row.createCell(1).setCellValue(m.getUserId());
            row.createCell(2).setCellValue(m.getTotalPurchases());
            row.createCell(3).setCellValue(m.getLifetimeValue().doubleValue());
            row.createCell(4).setCellValue(m.getSessionCount());
            row.createCell(5).setCellValue(m.getPageViewCount());
            row.createCell(6).setCellValue(m.getActive() != null && m.getActive() ? "Yes" : "No");
            row.createCell(7).setCellValue(m.getSegment() != null ? m.getSegment() : "");
            row.createCell(8).setCellValue(m.getActivityLevel() != null ? m.getActivityLevel() : "");
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void processScheduledExports() {
        List<Export> pendingExports = exportRepository.findByStatus("SCHEDULED");
        for (Export export : pendingExports) {
            if (export.getScheduledAt() != null && export.getScheduledAt() <= System.currentTimeMillis()) {
                log.info("Processing scheduled export: {}", export.getExportId());
                export.setStatus("PENDING");
                exportRepository.save(export);
                processExport(export.getId());

                if (export.getRecurring() != null && export.getRecurring()) {
                    // Create next scheduled export (24h later)
                    Export nextExport = Export.builder()
                        .exportId(UUID.randomUUID().toString())
                        .userId(export.getUserId())
                        .type(export.getType())
                        .format(export.getFormat())
                        .status("SCHEDULED")
                        .dateFrom(export.getDateFrom())
                        .dateTo(export.getDateTo())
                        .filters(export.getFilters())
                        .scheduledAt(export.getScheduledAt() + 86400000L)
                        .recurring(true)
                        .build();
                    exportRepository.save(nextExport);
                }
            }
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
        LocalDateTime createdAt = export.getCreatedAt() != null
            ? LocalDateTime.ofInstant(Instant.ofEpochMilli(export.getCreatedAt()), ZoneId.systemDefault())
            : null;
        LocalDateTime completedAt = export.getCompletedAt() != null
            ? LocalDateTime.ofInstant(Instant.ofEpochMilli(export.getCompletedAt()), ZoneId.systemDefault())
            : null;

        return ExportResponseDTO.builder()
            .exportId(export.getExportId())
            .status(export.getStatus())
            .type(export.getType())
            .format(export.getFormat())
            .filePath(export.getFilePath())
            .createdAt(createdAt)
            .completedAt(completedAt)
            .errorMessage(export.getErrorMessage())
            .build();
    }
}
