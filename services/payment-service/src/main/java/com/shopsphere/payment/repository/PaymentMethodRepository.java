package com.shopsphere.payment.repository;

import com.shopsphere.payment.model.PaymentMethodEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Payment Method Repository
 *
 * JPA repository for PaymentMethodEntity providing database operations.
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, String> {

    /**
     * Find all payment methods for a user
     */
    List<PaymentMethodEntity> findByUserId(String userId);

    /**
     * Find all payment methods for a user with pagination
     */
    Page<PaymentMethodEntity> findByUserId(String userId, Pageable pageable);

    /**
     * Find default payment method for a user
     */
    Optional<PaymentMethodEntity> findByUserIdAndIsDefaultTrue(String userId);

    /**
     * Find payment method by Stripe ID
     */
    Optional<PaymentMethodEntity> findByStripePaymentMethodId(String stripePaymentMethodId);

    /**
     * Check if user has saved payment methods
     */
    boolean existsByUserId(String userId);

    /**
     * Count payment methods for a user
     */
    long countByUserId(String userId);

    /**
     * Delete all payment methods for a user
     */
    void deleteByUserId(String userId);
}
