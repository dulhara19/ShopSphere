package com.shopsphere.inventory.repository;

import com.shopsphere.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByProductId(UUID productId);

    List<Inventory> findByStatus(Inventory.StockStatus status);

    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.lowStockThreshold AND i.status != 'OUT_OF_STOCK'")
    List<Inventory> findLowStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.quantity <= 0")
    List<Inventory> findOutOfStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.productId IN :productIds")
    List<Inventory> findByProductIdIn(@Param("productIds") List<UUID> productIds);

    @Query(value = "SELECT COUNT(*) FROM inventory WHERE status = :status", nativeQuery = true)
    Long countByStatus(@Param("status") String status);
}
