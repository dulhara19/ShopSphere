package com.shopsphere.payment.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Payment Method Response DTO
 *
 * Response body containing saved payment method information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodResponse {

    private String id;
    private String cardBrand;
    private String lastFourDigits;
    private Integer expiryMonth;
    private Integer expiryYear;
    private Boolean isDefault;
    private LocalDateTime createdAt;
}
