package com.shopsphere.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movement_log", indexes = {
        @Index(name = "idx_stock_movement_product", columnList = "product_id"),
        @Index(name = "idx_stock_movement_type", columnList = "change_type"),
        @Index(name = "idx_stock_movement_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementLog {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType changeType;

    @Column(name = "quantity_before", nullable = false)
    private Long quantityBefore;

    @Column(name = "quantity_after", nullable = false)
    private Long quantityAfter;

    @Column(name = "quantity_change", nullable = false)
    private Long quantityChange;

    @Column(name = "reason", length = 512)
    private String reason;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "reference_id")
    private UUID referenceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ChangeType {
        SALE,
        RETURN,
        RESTOCK,
        DAMAGE,
        ADJUSTMENT,
        RESERVE,
        RELEASE
    }
}
