package com.shopsphere.inventory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory", indexes = {
        @Index(name = "idx_product_id", columnList = "product_id", unique = true),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false, unique = true)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Long reservedQuantity;

    @Column(name = "low_stock_threshold", nullable = false)
    private Long lowStockThreshold;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StockStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    /**
     * Calculate available quantity (quantity - reserved)
     */
    @JsonIgnore
    public Long getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    /**
     * Check if product has sufficient stock
     */
    public boolean hasSufficientStock(Long requestedQuantity) {
        return getAvailableQuantity() >= requestedQuantity;
    }

    /**
     * Update status based on quantity
     */
    public void updateStatus() {
        if (quantity <= 0) {
            this.status = StockStatus.OUT_OF_STOCK;
        } else if (quantity <= lowStockThreshold) {
            this.status = StockStatus.LOW_STOCK;
        } else {
            this.status = StockStatus.IN_STOCK;
        }
    }

    public enum StockStatus {
        IN_STOCK,
        LOW_STOCK,
        OUT_OF_STOCK
    }
}
