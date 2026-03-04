package com.shopsphere.payment.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * Refund Request DTO
 *
 * Request body for processing a refund.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {

    private String paymentId;
    private BigDecimal amount;  // Optional: if null, full refund
    private String reason;      // CUSTOMER_REQUEST, DUPLICATE, FRAUDULENT, OTHER
}
