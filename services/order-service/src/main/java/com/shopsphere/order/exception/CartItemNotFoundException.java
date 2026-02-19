package com.shopsphere.order.exception;

import java.util.UUID;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(String message) {
        super(message);
    }

    public static CartItemNotFoundException forId(UUID itemId) {
        return new CartItemNotFoundException("Cart item not found with ID: " + itemId);
    }
}
