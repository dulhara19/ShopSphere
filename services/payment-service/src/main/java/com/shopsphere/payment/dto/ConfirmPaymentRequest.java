package com.shopsphere.payment.dto;

import lombok.*;

/**
 * Confirm Payment Request DTO
 *
 * Request body for confirming a payment intent.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmPaymentRequest {

    private String paymentId;
    private String paymentMethodId;  // Optional: if user wants to use different method
}
