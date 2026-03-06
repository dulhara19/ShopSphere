package com.shopsphere.user.repository;

import com.shopsphere.user.model.Address;
import com.shopsphere.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AddressRepository Interface - Data access layer for Address entity
 *
 * Responsibilities:
 * - CRUD operations on Address entities
 * - Find addresses by user
 * - Manage default address state
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    /**
     * Find all addresses for a specific user
     *
     * @param user the user entity
     * @return list of addresses for the user
     */
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);

    /**
     * Find the default address for a user
     *
     * @param user the user entity
     * @return Optional containing the default address if exists
     */
    Optional<Address> findByUserAndIsDefaultTrue(User user);

    /**
     * Check if a user has a default address
     *
     * @param user the user entity
     * @return true if a default address exists
     */
    boolean existsByUserAndIsDefaultTrue(User user);

    /**
     * Remove default flag from all user addresses
     *
     * @param user the user entity
     */
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user = :user")
    void resetDefaultAddresses(@Param("user") User user);

    /**
     * Count addresses for a user
     *
     * @param user the user entity
     * @return number of addresses
     */
    long countByUser(User user);
}

