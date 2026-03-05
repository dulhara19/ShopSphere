package com.example.shipping_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import jakarta.validation.constraints.*;

@Document(collection = "shipping")
@Data
public class Shipping {
    @Id
    private String id;
    @NotBlank(message = "Order ID cannot be empty")
    private String orderId;
    private String trackingNumber;
    @NotBlank(message = "Carrier is required")
    private String carrier; // FedEx, UPS, DHL
    private String status;  // PENDING, SHIPPED, DELIVERED
    @Positive(message = "Shipping cost must be a positive value")
    private Double shippingCost; // Shipping rate calculation
    private Double weight;
    private Double distance;
    @NotBlank(message = "Street address is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Zip code is required")
    @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters")
    private String zipCode;
}