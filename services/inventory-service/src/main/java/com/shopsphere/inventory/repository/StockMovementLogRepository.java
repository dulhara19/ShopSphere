package com.shopsphere.inventory.repository;

import com.shopsphere.inventory.model.StockMovementLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementLogRepository extends JpaRepository<StockMovementLog, UUID> {

    @Query("SELECT s FROM StockMovementLog s " +
            "WHERE s.productId = :productId " +
            "AND (:from IS NULL OR s.createdAt >= :from) " +
            "AND (:to IS NULL OR s.createdAt <= :to) " +
            "AND (:changeType IS NULL OR s.changeType = :changeType) " +
            "ORDER BY s.createdAt DESC")
    List<StockMovementLog> findHistory(
            @Param("productId") UUID productId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("changeType") StockMovementLog.ChangeType changeType
    );

    List<StockMovementLog> findByProductId(UUID productId);

    List<StockMovementLog> findByProductIdAndChangeTypeAndCreatedAtAfter(
            UUID productId,
            StockMovementLog.ChangeType changeType,
            LocalDateTime createdAt
    );

    List<StockMovementLog> findByCreatedAtAfter(LocalDateTime createdAt);
}
