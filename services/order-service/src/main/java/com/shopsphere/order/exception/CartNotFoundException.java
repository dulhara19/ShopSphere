package com.shopsphere.order.exception;

public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(String message) {
        super(message);
    }

    public static CartNotFoundException forUser(String userId) {
        return new CartNotFoundException("Cart not found for user: " + userId);
    }

    public static CartNotFoundException forSession(String sessionId) {
        return new CartNotFoundException("Cart not found for session: " + sessionId);
    }
}
