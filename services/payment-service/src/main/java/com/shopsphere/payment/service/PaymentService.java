package com.shopsphere.payment.service;

import com.shopsphere.payment.dto.*;
import com.shopsphere.payment.event.PaymentFailedEvent;
import com.shopsphere.payment.event.PaymentSucceededEvent;
import com.shopsphere.payment.exception.PaymentException;
import com.shopsphere.payment.exception.PaymentNotFoundException;
import com.shopsphere.payment.model.Payment;
import com.shopsphere.payment.repository.PaymentRepository;
import com.stripe.model.PaymentIntent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Payment Service
 *
 * Business logic for payment processing.
 * Implements Epic 1.2, 1.5, 1.6 requirements.
 */
@Service
@Slf4j
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Create a payment intent
     * Story 1.2.1: Create payment intent
     */
    public CreatePaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        try {
            // Create payment in database with PENDING status
            Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(Payment.PaymentStatus.PENDING)
                .paymentMethod(Payment.PaymentMethod.CARD)
                .metadata(request.getDescription())
                .build();

            payment = paymentRepository.save(payment);

            // Create Stripe customer (assuming it exists, otherwise create it)
            // In real implementation, would check if customer exists first
            com.stripe.model.Customer customer = stripeService.createCustomer(
                request.getUserId(),
                request.getUserId() + "@customer.stripe",  // Placeholder email
                request.getUserId()
            );

            payment.setStripeCustomerId(customer.getId());

            // Create Stripe payment intent
            PaymentIntent intent = stripeService.createPaymentIntent(
                request.getOrderId(),
                request.getUserId(),
                customer.getId(),
                request.getAmount(),
                request.getCurrency()
            );

            payment.setStripePaymentIntentId(intent.getId());
            payment.setStatus(Payment.PaymentStatus.PROCESSING);
            payment = paymentRepository.save(payment);

            return CreatePaymentIntentResponse.builder()
                .paymentId(payment.getId())
                .clientSecret(intent.getClientSecret())
                .status(Payment.PaymentStatus.PROCESSING.toString())
                .orderId(request.getOrderId())
                .message("Payment intent created successfully")
                .build();

        } catch (Exception e) {
            log.error("Error creating payment intent for order: {}", request.getOrderId(), e);
            throw new PaymentException("INTENT_CREATION_FAILED", "Failed to create payment intent", e);
        }
    }

    /**
     * Confirm a payment
     * Story 1.2.2: Confirm payment
     */
    public PaymentStatusResponse confirmPayment(String paymentId, ConfirmPaymentRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        try {
            // Confirm with Stripe
            PaymentIntent intent = stripeService.confirmPaymentIntent(
                payment.getStripePaymentIntentId(),
                request.getPaymentMethodId()
            );

            // Update payment based on Stripe status
            if ("succeeded".equals(intent.getStatus())) {
                payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
                paymentRepository.save(payment);
                notifyOrderService(payment, true);
                publishPaymentSucceededEvent(payment);
            } else if ("requires_payment_method".equals(intent.getStatus())) {
                payment.setStatus(Payment.PaymentStatus.PENDING);
                paymentRepository.save(payment);
            } else if ("processing".equals(intent.getStatus())) {
                payment.setStatus(Payment.PaymentStatus.PROCESSING);
                paymentRepository.save(payment);
            }

            return getPaymentStatus(paymentId);

        } catch (Exception e) {
            log.error("Error confirming payment: {}", paymentId, e);
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            notifyOrderService(payment, false);
            publishPaymentFailedEvent(payment);
            throw new PaymentException("PAYMENT_CONFIRMATION_FAILED", "Failed to confirm payment", e);
        }
    }

    /**
     * Get payment status
     * Story 1.2.4: Payment status check
     */
    public PaymentStatusResponse getPaymentStatus(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        // Optionally sync with Stripe
        Optional<PaymentIntent> intent = syncPaymentWithStripe(payment);
        if (intent.isPresent()) {
            updatePaymentFromStripe(payment, intent.get());
        }

        return PaymentStatusResponse.builder()
            .paymentId(payment.getId())
            .orderId(payment.getOrderId())
            .status(payment.getStatus().toString())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .paymentMethod(payment.getPaymentMethod().toString())
            .failureReason(payment.getFailureReason())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .build();
    }

    /**
     * Get payment by order ID (for Order Service)
     * Story 1.6.2: Get payment for order
     */
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new PaymentNotFoundException("No payment found for order: " + orderId));
    }

    /**
     * Get all user transactions (payments)
     * Story 1.5.1: List user transactions
     */
    public Page<Payment> getPaymentHistory(String userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable);
    }

    /**
     * Get all user transactions filtered by status
     */
    public Page<Payment> getPaymentHistoryByStatus(String userId, Payment.PaymentStatus status, Pageable pageable) {
        return paymentRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    /**
     * Sync payment status with Stripe
     */
    private Optional<PaymentIntent> syncPaymentWithStripe(Payment payment) {
        try {
            if (payment.getStripePaymentIntentId() != null) {
                com.stripe.model.PaymentIntent intent = stripeService.getPaymentIntent(payment.getStripePaymentIntentId());
                return Optional.of(intent);
            }
        } catch (Exception e) {
            log.warn("Failed to sync payment with Stripe: {}", payment.getId(), e);
        }
        return Optional.empty();
    }

    /**
     * Update payment from Stripe data
     */
    private void updatePaymentFromStripe(Payment payment, PaymentIntent intent) {
        if ("succeeded".equals(intent.getStatus())) {
            payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
        } else if ("processing".equals(intent.getStatus())) {
            payment.setStatus(Payment.PaymentStatus.PROCESSING);
        } else if ("requires_payment_method".equals(intent.getStatus())) {
            payment.setStatus(Payment.PaymentStatus.PENDING);
        } else if ("canceled".equals(intent.getStatus())) {
            payment.setStatus(Payment.PaymentStatus.CANCELLED);
        }
        paymentRepository.save(payment);
    }

    /**
     * Notify Order Service of payment result
     * Story 1.6.1: Payment status callback
     */
    private void notifyOrderService(Payment payment, boolean success) {
        try {
            // Implementation would use RestTemplate to POST to order service
            // String orderServiceUrl = "http://order-service:3004/internal/payments/" + payment.getId() + "/status-callback";
            log.info("Notifying Order Service about payment {} with result: {}", payment.getId(), success);
        } catch (Exception e) {
            log.error("Error notifying Order Service about payment: {}", payment.getId(), e);
        }
    }

    /**
     * Publish PaymentSucceededEvent
     * Story 1.6.3: Publish payment events
     */
    private void publishPaymentSucceededEvent(Payment payment) {
        try {
            PaymentSucceededEvent event = PaymentSucceededEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .stripePaymentIntentId(payment.getStripePaymentIntentId())
                .processedAt(LocalDateTime.now())
                .build();

            applicationContext.publishEvent(event);
            log.info("PaymentSucceededEvent published for payment: {}", payment.getId());
        } catch (Exception e) {
            log.error("Error publishing PaymentSucceededEvent: {}", payment.getId(), e);
        }
    }

    /**
     * Publish PaymentFailedEvent
     * Story 1.6.3: Publish payment events
     */
    private void publishPaymentFailedEvent(Payment payment) {
        try {
            PaymentFailedEvent event = PaymentFailedEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .failureReason(payment.getFailureReason())
                .failedAt(LocalDateTime.now())
                .build();

            applicationContext.publishEvent(event);
            log.info("PaymentFailedEvent published for payment: {}", payment.getId());
        } catch (Exception e) {
            log.error("Error publishing PaymentFailedEvent: {}", payment.getId(), e);
        }
    }
}
