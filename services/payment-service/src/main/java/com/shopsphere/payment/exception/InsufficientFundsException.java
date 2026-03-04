package com.shopsphere.payment.exception;

/**
 * Insufficient Funds Exception
 *
 * Thrown when refund amount exceeds payment amount.
 */
public class InsufficientFundsException extends PaymentException {

    public InsufficientFundsException(String paymentId) {
        super("INSUFFICIENT_FUNDS", "Insufficient funds to refund for payment: " + paymentId);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super("INSUFFICIENT_FUNDS", message, cause);
    }
}
