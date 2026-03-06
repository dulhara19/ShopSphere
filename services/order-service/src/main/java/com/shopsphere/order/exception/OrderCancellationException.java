package com.shopsphere.order.exception;

import com.shopsphere.order.model.OrderStatus;

public class OrderCancellationException extends RuntimeException {

    public OrderCancellationException(String message) {
        super(message);
    }

    public static OrderCancellationException cannotCancel(OrderStatus status) {
        return new OrderCancellationException(
            String.format("Order cannot be cancelled when status is %s", status)
        );
    }
}
