package com.shopsphere.payment.repository;

import com.shopsphere.payment.model.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Refund Repository
 *
 * JPA repository for Refund entity providing database operations.
 */
@Repository
public interface RefundRepository extends JpaRepository<Refund, String> {

    /**
     * Find all refunds for a payment
     */
    List<Refund> findByPaymentId(String paymentId);

    /**
     * Find refunds for a payment with pagination
     */
    Page<Refund> findByPaymentId(String paymentId, Pageable pageable);

    /**
     * Find refund by Stripe refund ID
     */
    Optional<Refund> findByStripeRefundId(String stripeRefundId);

    /**
     * Find refunds by status
     */
    Page<Refund> findByStatus(Refund.RefundStatus status, Pageable pageable);

    /**
     * Find pending refunds
     */
    List<Refund> findByStatusIn(List<Refund.RefundStatus> statuses);

    /**
     * Count refunds for a payment
     */
    long countByPaymentId(String paymentId);
}
