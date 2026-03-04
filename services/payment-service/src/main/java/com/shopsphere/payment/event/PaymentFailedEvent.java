package com.shopsphere.payment.event;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Failed Event
 *
 * Domain event published when a payment fails.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {

    private String paymentId;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String failureReason;
    private LocalDateTime failedAt;
}
