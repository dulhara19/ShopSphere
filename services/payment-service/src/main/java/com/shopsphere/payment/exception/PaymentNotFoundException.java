package com.shopsphere.payment.exception;

/**
 * Payment Not Found Exception
 *
 * Thrown when a payment is not found in the database.
 */
public class PaymentNotFoundException extends PaymentException {

    public PaymentNotFoundException(String paymentId) {
        super("PAYMENT_NOT_FOUND", "Payment not found with ID: " + paymentId);
    }

    public PaymentNotFoundException(String message, Throwable cause) {
        super("PAYMENT_NOT_FOUND", message, cause);
    }
}
