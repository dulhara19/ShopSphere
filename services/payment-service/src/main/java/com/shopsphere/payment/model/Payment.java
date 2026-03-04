package com.shopsphere.payment.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Entity
 *
 * Represents a payment transaction in the system.
 * Maps to 'payments' table in PostgreSQL.
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_stripe_payment_intent_id", columnList = "stripe_payment_intent_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private String id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String userId;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(columnDefinition = "jsonb")
    private String metadata;

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

    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        SUCCEEDED,
        FAILED,
        CANCELLED
    }

    public enum PaymentMethod {
        CARD,
        PAYPAL,
        CRYPTO,
        BANK_TRANSFER
    }
}
