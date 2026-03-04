package com.shopsphere.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportRequestDTO {

    @NotBlank(message = "Export type is required")
    private String type; // SALES, PRODUCTS, USERS, ORDERS

    @NotBlank(message = "Format is required")
    private String format; // CSV, EXCEL

    @NotNull(message = "Start date is required")
    private LocalDate dateFrom;

    @NotNull(message = "End date is required")
    private LocalDate dateTo;

    private Map<String, Object> filters;

    private Boolean scheduleEmail;

    private String emailRecipient;
}
