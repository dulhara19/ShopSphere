package com.shopsphere.payment.service;

import com.shopsphere.payment.dto.RefundRequest;
import com.shopsphere.payment.dto.RefundResponse;
import com.shopsphere.payment.event.RefundCompletedEvent;
import com.shopsphere.payment.exception.InsufficientFundsException;
import com.shopsphere.payment.exception.PaymentException;
import com.shopsphere.payment.exception.PaymentNotFoundException;
import com.shopsphere.payment.model.Payment;
import com.shopsphere.payment.model.Refund;
import com.shopsphere.payment.repository.PaymentRepository;
import com.shopsphere.payment.repository.RefundRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Refund Service
 *
 * Business logic for refund processing and lookup.
 * Implements Epic 1.4 (refund operations) and contributes to
 * Epic 1.5 transaction history by providing user refund queries.
 */
@Service
@Slf4j
@Transactional
public class RefundService {

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Process a refund
     * Stories 1.4.1, 1.4.2: Full and partial refunds
     */
    public RefundResponse processRefund(RefundRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
            .orElseThrow(() -> new PaymentNotFoundException(request.getPaymentId()));

        // Validate payment can be refunded
        if (!Payment.PaymentStatus.SUCCEEDED.equals(payment.getStatus())) {
            throw new PaymentException("REFUND_NOT_ALLOWED", "Payment must be in SUCCEEDED status to refund");
        }

        // Determine refund amount
        BigDecimal refundAmount = request.getAmount() != null ? request.getAmount() : payment.getAmount();

        // Validate refund amount doesn't exceed payment amount
        BigDecimal totalRefunded = calculateTotalRefunded(request.getPaymentId());
        if (totalRefunded.add(refundAmount).compareTo(payment.getAmount()) > 0) {
            throw new InsufficientFundsException(request.getPaymentId());
        }

        try {
            // Convert refund amount to cents for Stripe safely
            long amountInCents;
            try {
                amountInCents = refundAmount.movePointRight(2).longValueExact();
            } catch (ArithmeticException ex) {
                amountInCents = refundAmount.multiply(BigDecimal.valueOf(100)).longValue();
            }

            // Create refund with Stripe
            com.stripe.model.Refund stripeRefund = stripeService.createRefund(
                payment.getStripePaymentIntentId(),
                amountInCents,
                request.getReason()
            );

            // Map Stripe refund status to local enum
            String stripeStatus = stripeRefund != null ? stripeRefund.getStatus() : null;
            Refund.RefundStatus mappedStatus = "succeeded".equalsIgnoreCase(stripeStatus)
                ? Refund.RefundStatus.SUCCEEDED
                : ("failed".equalsIgnoreCase(stripeStatus) ? Refund.RefundStatus.FAILED : Refund.RefundStatus.PROCESSING);

            // Safely parse reason, default to OTHER when unknown
            Refund.RefundReason reasonEnum;
            try {
                reasonEnum = request.getReason() != null ? Refund.RefundReason.valueOf(request.getReason()) : Refund.RefundReason.OTHER;
            } catch (IllegalArgumentException ex) {
                reasonEnum = Refund.RefundReason.OTHER;
            }

            // Create refund in database
            Refund refund = Refund.builder()
                .paymentId(request.getPaymentId())
                .stripeRefundId(stripeRefund != null ? stripeRefund.getId() : null)
                .amount(refundAmount)
                .reason(reasonEnum)
                .status(mappedStatus)
                .build();

            refund = refundRepository.save(refund);

            // If Stripe already marked it succeeded, update payment status when fully refunded
            if (Refund.RefundStatus.SUCCEEDED.equals(mappedStatus) && totalRefunded.add(refundAmount).equals(payment.getAmount())) {
                payment.setStatus(Payment.PaymentStatus.CANCELLED);
                paymentRepository.save(payment);
            }

            publishRefundCompletedEvent(refund, payment);

            return RefundResponse.builder()
                .refundId(refund.getId())
                .paymentId(refund.getPaymentId())
                .amount(refund.getAmount())
                .reason(refund.getReason().toString())
                .status(refund.getStatus().toString())
                .createdAt(refund.getCreatedAt())
                .build();

        } catch (Exception e) {
            log.error("Error processing refund for payment: {}", request.getPaymentId(), e);
            throw new PaymentException("REFUND_PROCESSING_FAILED", "Failed to process refund", e);
        }
    }

    /**
     * Get refund details
     */
    public RefundResponse getRefund(String refundId) {
        Refund refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new PaymentNotFoundException("Refund not found: " + refundId));

        return RefundResponse.builder()
            .refundId(refund.getId())
            .paymentId(refund.getPaymentId())
            .amount(refund.getAmount())
            .reason(refund.getReason().toString())
            .status(refund.getStatus().toString())
            .failureReason(refund.getFailureReason())
            .createdAt(refund.getCreatedAt())
            .build();
    }

    /**
     * Get all refunds for a payment
     * Story 1.4.3: Refund status tracking
     */
    public Page<RefundResponse> getPaymentRefunds(String paymentId, Pageable pageable) {
        Page<Refund> refunds = refundRepository.findByPaymentId(paymentId, pageable);
        return refunds.map(refund -> RefundResponse.builder()
            .refundId(refund.getId())
            .paymentId(refund.getPaymentId())
            .amount(refund.getAmount())
            .reason(refund.getReason().toString())
            .status(refund.getStatus().toString())
            .failureReason(refund.getFailureReason())
            .createdAt(refund.getCreatedAt())
            .build());
    }

    /**
     * Handle refund webhook event
     * Story 1.4.4: Refund webhook handling
     */
    public void handleRefundWebhookEvent(String stripeRefundId, String status) {
        Refund.RefundStatus refundStatus = "succeeded".equals(status)
            ? Refund.RefundStatus.SUCCEEDED
            : Refund.RefundStatus.FAILED;

        refundRepository.findByStripeRefundId(stripeRefundId)
            .ifPresent(refund -> {
                refund.setStatus(refundStatus);
                refundRepository.save(refund);
                log.info("Refund status updated: {} with status: {}", stripeRefundId, status);
            });
    }

    /**
     * Calculate total refunded amount for a payment
     */
    private BigDecimal calculateTotalRefunded(String paymentId) {
        List<Refund> refunds = refundRepository.findByPaymentId(paymentId);
        return refunds.stream()
            .filter(r -> Refund.RefundStatus.SUCCEEDED.equals(r.getStatus()) || 
                         Refund.RefundStatus.PROCESSING.equals(r.getStatus()))
            .map(Refund::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get all refunds belonging to a user (used by transaction history)
     * Story 1.5.1: Transaction history should include refunds
     */
    public Page<RefundResponse> getUserRefunds(String userId, Pageable pageable) {
        Page<Refund> refunds = refundRepository.findByUserId(userId, pageable);
        return refunds.map(refund -> RefundResponse.builder()
            .refundId(refund.getId())
            .paymentId(refund.getPaymentId())
            .amount(refund.getAmount())
            .reason(refund.getReason().toString())
            .status(refund.getStatus().toString())
            .failureReason(refund.getFailureReason())
            .createdAt(refund.getCreatedAt())
            .build());
    }

    /**
     * Notify order service about a completed refund (optional enhancement)
     */
    private void notifyOrderServiceOfRefund(Refund refund) {
        try {
            // in a real implementation you would make a REST call to the order service
            log.info("Notifying Order Service about refund {} for payment {}", refund.getId(), refund.getPaymentId());
        } catch (Exception e) {
            log.error("Error notifying Order Service about refund: {}", refund.getId(), e);
        }
    }

    /**
     * Publish RefundCompletedEvent
     * Story 1.6.3: Publish payment events
     */
    private void publishRefundCompletedEvent(Refund refund, Payment payment) {
        try {
            RefundCompletedEvent event = RefundCompletedEvent.builder()
                .refundId(refund.getId())
                .paymentId(refund.getPaymentId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(refund.getAmount())
                .reason(refund.getReason().toString())
                .status(refund.getStatus().toString())
                .completedAt(LocalDateTime.now())
                .build();

            applicationContext.publishEvent(event);
            log.info("RefundCompletedEvent published for refund: {}", refund.getId());
            // also notify order service asynchronously
            notifyOrderServiceOfRefund(refund);
        } catch (Exception e) {
            log.error("Error publishing RefundCompletedEvent: {}", refund.getId(), e);
        }
    }
}
