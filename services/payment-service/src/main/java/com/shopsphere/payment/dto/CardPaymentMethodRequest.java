package com.shopsphere.payment.dto;

import lombok.*;

/**
 * Card Payment Method Request DTO
 *
 * Request body for saving a card payment method.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardPaymentMethodRequest {

    private String userId;
    private String cardToken;  // From Stripe Elements on frontend
    private Boolean setAsDefault;
}
