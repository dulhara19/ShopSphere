package com.shopsphere.payment.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Refund Entity
 *
 * Represents a refund transaction linked to a payment.
 * Maps to 'refunds' table in PostgreSQL.
 */
@Entity
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private String id;

    @Column(nullable = false)
    private String paymentId;

    @Column(name = "stripe_refund_id")
    private String stripeRefundId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum RefundReason {
        CUSTOMER_REQUEST,
        DUPLICATE,
        FRAUDULENT,
        OTHER
    }

    public enum RefundStatus {
        PENDING,
        PROCESSING,
        SUCCEEDED,
        FAILED
    }
}
