package com.shopsphere.payment.repository;

import com.shopsphere.payment.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Payment Repository
 *
 * JPA repository for Payment entity providing database operations.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    /**
     * Find payment by order ID
     */
    Optional<Payment> findByOrderId(String orderId);

    /**
     * Find payment by Stripe PaymentIntent ID
     */
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Find all payments for a user with pagination
     */
    Page<Payment> findByUserId(String userId, Pageable pageable);

    /**
     * Find all payments for a user by status
     */
    Page<Payment> findByUserIdAndStatus(String userId, Payment.PaymentStatus status, Pageable pageable);

    /**
     * Find all payments by status
     */
    Page<Payment> findByStatus(Payment.PaymentStatus status, Pageable pageable);

    /**
     * Find payments created within a date range
     */
    Page<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find payments for a user created within a date range
     */
    Page<Payment> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Check if payment exists for order ID
     */
    boolean existsByOrderId(String orderId);
}
