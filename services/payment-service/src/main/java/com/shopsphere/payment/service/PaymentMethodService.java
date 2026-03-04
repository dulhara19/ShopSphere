package com.shopsphere.payment.service;

import com.shopsphere.payment.dto.CardPaymentMethodRequest;
import com.shopsphere.payment.dto.PaymentMethodResponse;
import com.shopsphere.payment.exception.PaymentException;
import com.shopsphere.payment.model.PaymentMethodEntity;
import com.shopsphere.payment.repository.PaymentMethodRepository;
import com.stripe.model.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Payment Method Service
 *
 * Business logic for managing saved payment methods.
 * Implements Epic 1.3 requirements.
 */
@Service
@Slf4j
@Transactional
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private StripeService stripeService;

    /**
     * Save a new card payment method
     * Story 1.3.1: Save card for future use
     */
    public PaymentMethodResponse savePaymentMethod(String userId, CardPaymentMethodRequest request) {
        try {
            // Get or create Stripe customer
            // In real implementation, would need to get existing customer ID
            // For now, assume customer was created during payment intent creation

            // Attach payment method to customer
            PaymentMethod paymentMethod = stripeService.savePaymentMethod(
                userId,  // This should be customerId, not userId
                request.getCardToken()
            );

            // Save to database
            PaymentMethodEntity methodEntity = PaymentMethodEntity.builder()
                .userId(userId)
                .stripePaymentMethodId(paymentMethod.getId())
                .cardBrand(paymentMethod.getCard().getBrand())
                .lastFourDigits(paymentMethod.getCard().getLast4())
                .expiryMonth(Math.toIntExact(paymentMethod.getCard().getExpMonth()))
                .expiryYear(Math.toIntExact(paymentMethod.getCard().getExpYear()))
                .isDefault(request.getSetAsDefault() != null && request.getSetAsDefault())
                .build();

            methodEntity = paymentMethodRepository.save(methodEntity);

            return mapToResponse(methodEntity);

        } catch (Exception e) {
            log.error("Error saving payment method for user: {}", userId, e);
            throw new PaymentException("SAVE_PAYMENT_METHOD_FAILED", "Failed to save payment method", e);
        }
    }

    /**
     * Get all payment methods for a user
     * Story 1.3.2: List saved cards
     */
    public Page<PaymentMethodResponse> getUserPaymentMethods(String userId, Pageable pageable) {
        Page<PaymentMethodEntity> methods = paymentMethodRepository.findByUserId(userId, pageable);
        return methods.map(this::mapToResponse);
    }

    /**
     * Get default payment method for a user
     */
    public PaymentMethodResponse getDefaultPaymentMethod(String userId) {
        return paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId)
            .map(this::mapToResponse)
            .orElseThrow(() -> new PaymentException("NO_DEFAULT_PAYMENT_METHOD", "User has no default payment method"));
    }

    /**
     * Delete a payment method
     * Story 1.3.4: Remove saved card
     */
    public void deletePaymentMethod(String paymentMethodId) {
        PaymentMethodEntity method = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new PaymentException("PAYMENT_METHOD_NOT_FOUND", "Payment method not found"));

        try {
            // Delete from Stripe
            stripeService.deletePaymentMethod(method.getStripePaymentMethodId());

            // Delete from database
            paymentMethodRepository.delete(method);

            log.info("Payment method deleted: {}", paymentMethodId);

        } catch (Exception e) {
            log.error("Error deleting payment method: {}", paymentMethodId, e);
            throw new PaymentException("DELETE_PAYMENT_METHOD_FAILED", "Failed to delete payment method", e);
        }
    }

    /**
     * Set default payment method
     * Story 1.3.5: Set default card
     */
    public PaymentMethodResponse setDefaultPaymentMethod(String userId, String paymentMethodId) {
        PaymentMethodEntity method = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new PaymentException("PAYMENT_METHOD_NOT_FOUND", "Payment method not found"));

        if (!method.getUserId().equals(userId)) {
            throw new PaymentException("UNAUTHORIZED", "Payment method does not belong to this user");
        }

        try {
            // Remove current default
            paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(current -> {
                    current.setIsDefault(false);
                    paymentMethodRepository.save(current);
                });

            // Set new default
            method.setIsDefault(true);
            method = paymentMethodRepository.save(method);

            log.info("Default payment method set for user: {}", userId);
            return mapToResponse(method);

        } catch (Exception e) {
            log.error("Error setting default payment method: {}", paymentMethodId, e);
            throw new PaymentException("SET_DEFAULT_FAILED", "Failed to set default payment method", e);
        }
    }

    /**
     * Map entity to response DTO
     */
    private PaymentMethodResponse mapToResponse(PaymentMethodEntity entity) {
        return PaymentMethodResponse.builder()
            .id(entity.getId())
            .cardBrand(entity.getCardBrand())
            .lastFourDigits(entity.getLastFourDigits())
            .expiryMonth(entity.getExpiryMonth())
            .expiryYear(entity.getExpiryYear())
            .isDefault(entity.getIsDefault())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
