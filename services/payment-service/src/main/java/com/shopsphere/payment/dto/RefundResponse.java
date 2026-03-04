package com.shopsphere.payment.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Refund Response DTO
 *
 * Response body containing refund information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponse {

    private String refundId;
    private String paymentId;
    private BigDecimal amount;
    private String reason;
    private String status;
    private String failureReason;
    private LocalDateTime createdAt;
}
