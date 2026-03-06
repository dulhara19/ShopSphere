package com.shopsphere.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportResponseDTO {

    private String exportId;

    private String status; // PENDING, PROCESSING, COMPLETED, FAILED

    private String type;

    private String format;

    private String filePath;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private String errorMessage;

    private String downloadUrl;
}
