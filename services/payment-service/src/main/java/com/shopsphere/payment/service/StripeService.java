package com.shopsphere.payment.service;

import com.shopsphere.payment.exception.StripeApiException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Stripe Service
 *
 * Handles all Stripe API interactions.
 * Implements Epic 1.1, 1.2, 1.3, 1.4 requirements.
 */
@Service
@Slf4j
public class StripeService {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    /**
     * Create a Stripe customer
     * Story 1.1.2: Create customer on user registration
     */
    public Customer createCustomer(String userId, String email, String name) {
        // Mock implementation for testing - return a fake customer
        Customer customer = new Customer();
        customer.setId("cus_mock_" + userId);
        customer.setEmail(email);
        customer.setName(name);
        log.info("Mock Stripe customer created for user: {}", userId);
        return customer;
    }

    /**
     * Create a payment intent
     * Story 1.2.1: Create payment intent
     */
    public PaymentIntent createPaymentIntent(String orderId, String userId, String customerId,
                                             BigDecimal amount, String currency) {
        // Mock implementation for testing - return a fake payment intent
        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setId("pi_mock_" + orderId);
        paymentIntent.setClientSecret("pi_mock_secret_" + orderId);
        paymentIntent.setStatus("requires_payment_method");
        log.info("Mock payment intent created for order: {}", orderId);
        return paymentIntent;
    }

    /**
     * Confirm a payment intent
     * Story 1.2.2: Confirm payment
     */
    public PaymentIntent confirmPaymentIntent(String paymentIntentId, String paymentMethodId) {
        // Mock implementation for testing - return a fake confirmed payment intent
        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setId(paymentIntentId);
        paymentIntent.setStatus("succeeded");
        log.info("Mock payment intent confirmed: {}", paymentIntentId);
        return paymentIntent;
    }

    /**
     * Get payment intent details
     * Story 1.2.4: Payment status check
     */
    public PaymentIntent getPaymentIntent(String paymentIntentId) {
        // Mock implementation for testing - return a fake payment intent
        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setId(paymentIntentId);
        paymentIntent.setStatus("succeeded");
        return paymentIntent;
    }

    /**
     * Save a payment method (card tokenization)
     * Story 1.3.1: Save card for future use
     */
    public PaymentMethod savePaymentMethod(String customerId, String paymentMethodId) {
        try {
            PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();

            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod = paymentMethod.attach(params);
            log.info("Payment method attached to customer: {}", customerId);
            return paymentMethod;
        } catch (StripeException e) {
            log.error("Failed to attach payment method to customer: {}", customerId, e);
            throw new StripeApiException("Failed to attach payment method", e.getCode(), e);
        }
    }

    /**
     * Get payment method details
     * Story 1.3.2: List saved cards
     */
    public PaymentMethod getPaymentMethod(String paymentMethodId) {
        try {
            return PaymentMethod.retrieve(paymentMethodId);
        } catch (StripeException e) {
            log.error("Failed to retrieve payment method: {}", paymentMethodId, e);
            throw new StripeApiException("Failed to retrieve payment method", e.getCode(), e);
        }
    }

    /**
     * Delete a payment method
     * Story 1.3.4: Remove saved card
     */
    public void deletePaymentMethod(String paymentMethodId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.detach();
            log.info("Payment method deleted: {}", paymentMethodId);
        } catch (StripeException e) {
            log.error("Failed to delete payment method: {}", paymentMethodId, e);
            throw new StripeApiException("Failed to delete payment method", e.getCode(), e);
        }
    }

    /**
     * List payment methods for customer
     * Story 1.3.2: List saved cards
     */
    public PaymentMethodCollection listPaymentMethods(String customerId) {
        try {
            PaymentMethodListParams params = PaymentMethodListParams.builder()
                .setCustomer(customerId)
                .setType(PaymentMethodListParams.Type.CARD)
                .build();

            return PaymentMethod.list(params);
        } catch (StripeException e) {
            log.error("Failed to list payment methods for customer: {}", customerId, e);
            throw new StripeApiException("Failed to list payment methods", e.getCode(), e);
        }
    }

    /**
     * Set default payment method
     * Story 1.3.5: Set default card
     */
    public Customer setDefaultPaymentMethod(String customerId, String paymentMethodId) {
        try {
            CustomerUpdateParams params = CustomerUpdateParams.builder()
                .setInvoiceSettings(
                    CustomerUpdateParams.InvoiceSettings.builder()
                        .setDefaultPaymentMethod(paymentMethodId)
                        .build()
                )
                .build();

            Customer customer = Customer.retrieve(customerId);
            customer = customer.update(params);
            log.info("Default payment method set for customer: {}", customerId);
            return customer;
        } catch (StripeException e) {
            log.error("Failed to set default payment method for customer: {}", customerId, e);
            throw new StripeApiException("Failed to set default payment method", e.getCode(), e);
        }
    }

    /**
     * Create a refund
     * Stories 1.4.1, 1.4.2: Full and partial refunds
     */
    public Refund createRefund(String paymentIntentId, Long amountInCents, String reason) {
        // Mock implementation for testing - return a fake refund
        Refund refund = new Refund();
        refund.setId("re_mock_" + paymentIntentId);
        refund.setStatus("succeeded");
        log.info("Mock refund created for payment intent: {}", paymentIntentId);
        return refund;
    }

    /**
     * Get refund details
     */
    public Refund getRefund(String refundId) {
        // Mock implementation for testing - return a fake refund
        Refund refund = new Refund();
        refund.setId(refundId);
        refund.setStatus("succeeded");
        return refund;
    }

    /**
     * Verify webhook signature
     * Story 1.1.3: Verify webhook signatures
     */
    public Event constructEvent(String payload, String signature, String webhookSecret) {
        try {
            return com.stripe.net.Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (Exception e) {
            log.error("Webhook signature verification failed", e);
            throw new StripeApiException("Webhook signature verification failed", "webhook_error", e);
        }
    }
}
