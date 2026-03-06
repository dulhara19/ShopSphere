package com.shopsphere.user.dto;

import com.shopsphere.user.model.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * UpdateUserRoleRequest DTO - Request object for updating user roles
 *
 * Used by admin endpoints to change a user's roles.
 * Admin can assign multiple roles to a user (e.g., SELLER, CUSTOMER).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRoleRequest {

    /**
     * Set of roles to assign to the user
     * At least one role must be provided
     */
    @NotEmpty(message = "Roles cannot be empty")
    private Set<Role> roles;
}

