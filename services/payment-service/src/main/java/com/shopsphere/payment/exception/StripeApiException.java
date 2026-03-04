package com.shopsphere.payment.exception;

/**
 * Stripe API Exception
 *
 * Thrown when Stripe API returns an error.
 */
public class StripeApiException extends PaymentException {

    private final String stripeErrorCode;

    public StripeApiException(String message, String stripeErrorCode) {
        super("STRIPE_API_ERROR", message);
        this.stripeErrorCode = stripeErrorCode;
    }

    public StripeApiException(String message, String stripeErrorCode, Throwable cause) {
        super("STRIPE_API_ERROR", message, cause);
        this.stripeErrorCode = stripeErrorCode;
    }

    public String getStripeErrorCode() {
        return stripeErrorCode;
    }
}
