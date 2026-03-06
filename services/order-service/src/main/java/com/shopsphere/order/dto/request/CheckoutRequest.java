package com.shopsphere.order.dto.request;

import com.shopsphere.order.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    private UUID shippingAddressId;

    private UUID billingAddressId;

    private PaymentMethod paymentMethod;

    // Inline address for when no saved address is used
    private ShippingAddress shippingAddress;

    private String shippingMethodId;

    private String paymentMethodId;

    private String couponCode;

    private String notes;

    private String idempotencyKey;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingAddress {
        private String firstName;
        private String lastName;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String phone;
    }
}
