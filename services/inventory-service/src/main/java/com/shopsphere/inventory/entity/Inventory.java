package com.shopsphere.inventory.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String productId;

    private int quantity;
    private int reservedQuantity;
    private int lowStockThreshold;

    private Instant createdAt;
    private Instant lastUpdated;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
        lastUpdated = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        lastUpdated = Instant.now();
    }

    // getters/setters omitted for now
}
