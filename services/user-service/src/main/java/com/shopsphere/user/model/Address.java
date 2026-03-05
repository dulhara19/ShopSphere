package com.shopsphere.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Address Entity - Represents a user's address
 *
 * Responsibilities:
 * - Stores user address information
 * - Maintains relationship with User entity
 * - Tracks default address flag per user
 * - Audits creation and modification timestamps
 */
@Entity
@Table(name = "addresses", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_user_id_is_default", columnList = "user_id, is_default")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    /**
     * Unique identifier for the address (UUID v4)
     */
    @Id
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    /**
     * Reference to the User who owns this address
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Street address line
     */
    @Column(name = "street", nullable = false, length = 255)
    private String street;

    /**
     * City name
     */
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    /**
     * State or province
     */
    @Column(name = "state", nullable = false, length = 100)
    private String state;

    /**
     * Postal or ZIP code
     */
    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    /**
     * Country name
     */
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    /**
     * Flag indicating if this is the user's default address
     */
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    /**
     * Timestamp when address was created
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when address was last updated
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Generate a new UUID if not already set
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}

