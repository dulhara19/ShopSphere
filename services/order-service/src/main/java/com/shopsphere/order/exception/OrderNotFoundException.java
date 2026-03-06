package com.shopsphere.order.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public static OrderNotFoundException forId(UUID orderId) {
        return new OrderNotFoundException("Order not found with ID: " + orderId);
    }

    public static OrderNotFoundException forOrderNumber(String orderNumber) {
        return new OrderNotFoundException("Order not found with number: " + orderNumber);
    }
}
