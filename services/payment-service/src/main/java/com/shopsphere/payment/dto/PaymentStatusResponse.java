package com.shopsphere.payment.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Status Response DTO
 *
 * Response body containing payment status information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusResponse {

    private String paymentId;
    private String orderId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
