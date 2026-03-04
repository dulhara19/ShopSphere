package com.shopsphere.inventory.repository;

import com.shopsphere.inventory.model.Warehouse;
import com.shopsphere.inventory.model.WarehouseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory, UUID> {

    Optional<WarehouseInventory> findByProductIdAndWarehouse(UUID productId, Warehouse warehouse);

    List<WarehouseInventory> findByProductId(UUID productId);

    @Query("SELECT COALESCE(SUM(wi.quantity), 0) FROM WarehouseInventory wi WHERE wi.productId = :productId")
    Long sumQuantityByProductId(@Param("productId") UUID productId);
}
