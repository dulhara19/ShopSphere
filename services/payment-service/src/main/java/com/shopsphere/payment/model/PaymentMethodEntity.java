package com.shopsphere.payment.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * PaymentMethod Entity
 *
 * Represents a saved payment method (card) for a user.
 * Maps to 'payment_methods' table in PostgreSQL.
 */
@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(name = "stripe_payment_method_id", nullable = false)
    private String stripePaymentMethodId;

    @Column(nullable = false)
    private String cardBrand;

    @Column(nullable = false)
    private String lastFourDigits;

    @Column(nullable = false)
    private Integer expiryMonth;

    @Column(nullable = false)
    private Integer expiryYear;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

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
}
