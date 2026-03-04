package com.shopsphere.payment.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * Create Payment Intent Request DTO
 *
 * Request body for creating a payment intent.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentIntentRequest {

    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String userId;
    private String paymentMethodId;  // Optional: for saving card
    private Boolean saveCard;        // Optional: save card for future use
    private String description;
}
