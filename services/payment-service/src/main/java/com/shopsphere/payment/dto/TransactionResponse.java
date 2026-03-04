package com.shopsphere.payment.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction Response DTO
 *
 * Response body containing transaction details for history/reporting.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private String transactionId;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String type;  // PAYMENT or REFUND
    private String status;
    private String description;
    private LocalDateTime createdAt;
}
