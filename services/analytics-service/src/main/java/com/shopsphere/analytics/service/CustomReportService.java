package com.shopsphere.analytics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.analytics.model.CustomReport;
import com.shopsphere.analytics.repository.CustomReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomReportService {

    private final CustomReportRepository customReportRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomReport saveReport(CustomReport report) {
        report.setCreatedAt(LocalDateTime.now());
        return customReportRepository.save(report);
    }

    public List<CustomReport> getUserReports(String userId) {
        return customReportRepository.findByAuthorUserId(userId);
    }

    public CustomReport getReport(Long id) {
        return customReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public CustomReport scheduleReport(Long id, String cron, String emails) {
        CustomReport report = getReport(id);
        report.setScheduled(true);
        report.setScheduleCron(cron);
        report.setEmailRecipients(emails);
        return customReportRepository.save(report);
    }

    public Map<String, Object> runReport(Long id) {
        CustomReport report = getReport(id);
        log.info("Running custom report: {}", report.getReportName());

        // In a real implementation, we would parse the configurationJson (which
        // contains dimensions, metrics, and filters)
        // and dynamically construct a SQL/ClickHouse query to execute.
        // For demonstration purposes, returning a simulated dataset map.

        report.setLastRunAt(LocalDateTime.now());
        customReportRepository.save(report);

        return Map.of(
                "reportName", report.getReportName(),
                "generatedAt", LocalDateTime.now(),
                "data", List.of(
                        Map.of("dimension", "Category A", "metric", 1500),
                        Map.of("dimension", "Category B", "metric", 3200)));
    }
}
