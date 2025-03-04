package com.shopsphere.order.exception;

public class MaxCartItemsExceededException extends RuntimeException {

    public MaxCartItemsExceededException(int maxItems) {
        super("Cart cannot have more than " + maxItems + " items");
    }
}
