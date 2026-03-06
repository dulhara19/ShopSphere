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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * User Entity - Core user data model for ShopSphere.
 *
 * Responsibilities:
 * - Stores user authentication credentials
 * - Manages user profile information
 * - Tracks user roles and permissions
 * - Audits creation and modification timestamps
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_username", columnList = "username", unique = true),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique identifier for the user (UUID v4)
     */
    @Id
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    /**
     * Unique username for login
     */
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    /**
     * Unique email address (also used for login)
     */
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    /**
     * BCrypt hashed password
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * User's first name
     */
    @Column(name = "first_name", length = 50)
    private String firstName;

    /**
     * User's last name
     */
    @Column(name = "last_name", length = 50)
    private String lastName;

    /**
     * User's phone number
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * User's residential address
     */
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    /**
     * City of residence
     */
    @Column(name = "city", length = 50)
    private String city;

    /**
     * State/Province of residence
     */
    @Column(name = "state", length = 50)
    private String state;

    /**
     * Postal code
     */
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /**
     * Country of residence
     */
    @Column(name = "country", length = 50)
    private String country;

    /**
     * User's profile picture URL
     */
    @Column(name = "profile_picture_url", columnDefinition = "TEXT")
    private String profilePictureUrl;

    /**
     * Flag indicating if user account is enabled/active
     */
    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private Boolean isEnabled = true;

    /**
     * Flag indicating if email has been verified
     */
    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private Boolean isEmailVerified = false;

    /**
     * Flag indicating if account is locked (too many failed login attempts)
     */
    @Column(name = "is_account_locked", nullable = false)
    @Builder.Default
    private Boolean isAccountLocked = false;

    /**
     * Counter for failed login attempts
     */
    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    /**
     * Timestamp of last successful login
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * Collection of roles assigned to this user
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Collection of addresses associated with this user (one-to-many relationship)
     * Cascade delete ensures addresses are deleted when user is deleted
     * Lazy loading is used to avoid loading all addresses with every user fetch
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Address> addresses;

    /**
     * Timestamp when user account was created
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when user account was last updated
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Timestamp when user account was deleted (soft delete)
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * User-friendly full name combining first and last names
     */
    @Transient
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return username;
        }
    }

    /**
     * Check if user has a specific role
     */
    @Transient
    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Check if user is an admin
     */
    @Transient
    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    /**
     * Check if user is a seller
     */
    @Transient
    public boolean isSeller() {
        return hasRole(Role.SELLER);
    }

    /**
     * Check if user is a customer
     */
    @Transient
    public boolean isCustomer() {
        return hasRole(Role.CUSTOMER);
    }

    /**
     * Check if account is active (not deleted, not locked, enabled)
     */
    @Transient
    public boolean isActive() {
        return isEnabled && !isAccountLocked && deletedAt == null;
    }

}

