package com.shopsphere.payment.dto;

import lombok.*;

/**
 * Create Payment Intent Response DTO
 *
 * Response body containing payment intent details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentIntentResponse {

    private String paymentId;
    private String clientSecret;
    private String status;
    private String orderId;
    private String message;
}
