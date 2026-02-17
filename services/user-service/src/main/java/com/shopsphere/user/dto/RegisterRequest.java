package com.shopsphere.user.dto;

}
    private Set<Role> roles = new HashSet<>(Set.of(Role.CUSTOMER));
    @Builder.Default
     */
     * Defaults to CUSTOMER role if not specified
     * Set of roles for the user
    /**

    private String password;
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @NotBlank(message = "Password is required")
     */
     * User's password (plain text, will be hashed during registration)
    /**

    private String email;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
     */
     * User's email address (used as username for login)
    /**

    private String lastName;
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @NotBlank(message = "Last name is required")
     */
     * User's last name
    /**

    private String firstName;
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @NotBlank(message = "First name is required")
     */
     * User's first name
    /**

public class RegisterRequest {
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
 */
 * Handles incoming registration data with validation.
 *
 * RegisterRequest DTO - Request object for user registration.
/**

import java.util.Set;
import java.util.HashSet;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import com.shopsphere.user.model.Role;

