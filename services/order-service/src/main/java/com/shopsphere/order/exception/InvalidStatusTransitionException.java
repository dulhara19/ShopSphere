package com.shopsphere.order.exception;

import com.shopsphere.order.model.OrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    public static InvalidStatusTransitionException of(OrderStatus from, OrderStatus to) {
        return new InvalidStatusTransitionException(
            String.format("Invalid status transition from %s to %s", from, to)
        );
    }
}
