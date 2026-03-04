package com.shopsphere.payment.event;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Succeeded Event
 *
 * Domain event published when a payment is successfully processed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSucceededEvent {

    private String paymentId;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String stripePaymentIntentId;
    private LocalDateTime processedAt;
}
