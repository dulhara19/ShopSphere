package com.shopsphere.inventory.repository;

import com.shopsphere.inventory.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {

    List<StockReservation> findByProductId(UUID productId);

    List<StockReservation> findByOrderId(UUID orderId);

    List<StockReservation> findByStatus(StockReservation.ReservationStatus status);

    @Query("SELECT sr FROM StockReservation sr WHERE sr.status = 'PENDING' AND sr.expiresAt < :now")
    List<StockReservation> findExpiredReservations(@Param("now") LocalDateTime now);

    @Query("SELECT sr FROM StockReservation sr WHERE sr.productId = :productId AND sr.status = 'PENDING'")
    List<StockReservation> findPendingReservationsByProductId(@Param("productId") UUID productId);

    @Query("SELECT COALESCE(SUM(sr.quantity), 0) FROM StockReservation sr WHERE sr.productId = :productId AND sr.status = 'PENDING'")
    Long getTotalPendingQuantity(@Param("productId") UUID productId);
}
