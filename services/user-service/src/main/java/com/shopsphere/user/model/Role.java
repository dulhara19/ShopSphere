package com.shopsphere.user.model;

import lombok.Getter;

/**
 * Role Enum - Defines user roles in the ShopSphere platform.
 *
 * Roles:
 * - CUSTOMER: Regular user who purchases products
 * - SELLER: User who sells products (Vendor)
 * - ADMIN: Platform administrator with full system access
 */
@Getter
public enum Role {
    CUSTOMER("ROLE_CUSTOMER", "Customer - Regular user"),
    SELLER("ROLE_SELLER", "Seller - Vendor account"),
    ADMIN("ROLE_ADMIN", "Admin - System administrator");

    private final String authority;
    private final String description;

    Role(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }
}


