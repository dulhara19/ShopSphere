package com.shopsphere.payment.service;

import com.shopsphere.payment.config.StripeConfiguration;
import com.shopsphere.payment.exception.PaymentException;
import com.shopsphere.payment.model.Payment;
import com.shopsphere.payment.repository.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Webhook Service
 *
 * Handles Stripe webhook events.
 * Implements Epic 1.1 Story 1.1.3 and 1.4.4 requirements.
 */
@Service
@Slf4j
@Transactional
public class WebhookService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RefundService refundService;

    @Autowired
    private StripeConfiguration stripeConfiguration;

    /**
     * Verify webhook signature
     */
    public Event verifyWebhookSignature(String payload, String signature) {
        try {
            StripeService stripeService = new StripeService();
            return stripeService.constructEvent(payload, signature, stripeConfiguration.getWebhookSecret());
        } catch (Exception e) {
            log.error("Webhook signature verification failed", e);
            throw new PaymentException("WEBHOOK_VERIFICATION_FAILED", "Invalid webhook signature", e);
        }
    }

    /**
     * Process Stripe webhook event
     * Story 1.1.3: Handle event types
     * Story 1.4.4: Handle refund webhook events
     */
    public void processWebhookEvent(Event event) {
        try {
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                case "payment_intent.canceled":
                    handlePaymentIntentCanceled(event);
                    break;
                case "charge.refunded":
                    handleChargeRefunded(event);
                    break;
                case "refund.succeeded":
                    handleRefundSucceeded(event);
                    break;
                case "refund.failed":
                    handleRefundFailed(event);
                    break;
                default:
                    log.info("Unhandled webhook event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing webhook event: {}", event.getId(), e);
            throw new PaymentException("WEBHOOK_PROCESSING_FAILED", "Failed to process webhook event", e);
        }
    }

    /**
     * Handle payment_intent.succeeded event
     */
    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
            .getObject()
            .orElse(null);

        if (paymentIntent == null) {
            log.warn("Payment intent is null in webhook event");
            return;
        }

        String paymentIntentId = paymentIntent.getId();
        paymentRepository.findByStripePaymentIntentId(paymentIntentId)
            .ifPresent(payment -> {
                payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
                paymentRepository.save(payment);
                log.info("Payment succeeded: {}", payment.getId());
            });
    }

    /**
     * Handle payment_intent.payment_failed event
     */
    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
            .getObject()
            .orElse(null);

        if (paymentIntent == null) {
            log.warn("Payment intent is null in webhook event");
            return;
        }

        String paymentIntentId = paymentIntent.getId();
        String failureReason = paymentIntent.getLastPaymentError() != null ?
            paymentIntent.getLastPaymentError().getMessage() : "Unknown";

        paymentRepository.findByStripePaymentIntentId(paymentIntentId)
            .ifPresent(payment -> {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason(failureReason);
                paymentRepository.save(payment);
                log.info("Payment failed: {} - {}", payment.getId(), failureReason);
            });
    }

    /**
     * Handle payment_intent.canceled event
     */
    private void handlePaymentIntentCanceled(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
            .getObject()
            .orElse(null);

        if (paymentIntent == null) {
            log.warn("Payment intent is null in webhook event");
            return;
        }

        String paymentIntentId = paymentIntent.getId();
        paymentRepository.findByStripePaymentIntentId(paymentIntentId)
            .ifPresent(payment -> {
                payment.setStatus(Payment.PaymentStatus.CANCELLED);
                paymentRepository.save(payment);
                log.info("Payment cancelled: {}", payment.getId());
            });
    }

    /**
     * Handle charge.refunded event
     */
    private void handleChargeRefunded(Event event) {
        log.info("Charge refunded event received: {}", event.getId());
        // Update payment or refund status as needed
    }

    /**
     * Handle refund.succeeded event
     * Story 1.4.4: Handle refund.succeeded event
     */
    private void handleRefundSucceeded(Event event) {
        Refund refund = (Refund) event.getDataObjectDeserializer()
            .getObject()
            .orElse(null);

        if (refund == null) {
            log.warn("Refund is null in webhook event");
            return;
        }

        refundService.handleRefundWebhookEvent(refund.getId(), "succeeded");
        log.info("Refund succeeded: {}", refund.getId());
    }

    /**
     * Handle refund.failed event
     * Story 1.4.4: Handle refund.failed event
     */
    private void handleRefundFailed(Event event) {
        Refund refund = (Refund) event.getDataObjectDeserializer()
            .getObject()
            .orElse(null);

        if (refund == null) {
            log.warn("Refund is null in webhook event");
            return;
        }

        refundService.handleRefundWebhookEvent(refund.getId(), "failed");
        log.warn("Refund failed: {}", refund.getId());
    }
}
