package com.shopsphere.shipping.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import jakarta.validation.constraints.*;

@Document(collection = "shipping")
@Data
public class ShippingDTO {
    private String orderId;
    private String trackingNumber;
    private String carrier;
    private String status;
    private Double shippingCost;
    private String street;
    private String city;
    private String zipCode;
}