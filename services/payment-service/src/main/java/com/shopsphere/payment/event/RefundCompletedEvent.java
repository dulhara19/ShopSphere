package com.shopsphere.payment.event;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Refund Completed Event
 *
 * Domain event published when a refund is completed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundCompletedEvent {

    private String refundId;
    private String paymentId;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String reason;
    private String status;
    private LocalDateTime completedAt;
}
