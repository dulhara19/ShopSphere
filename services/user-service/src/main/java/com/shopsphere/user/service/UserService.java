package com.shopsphere.user.service;

import com.shopsphere.user.dto.UpdateUserRoleRequest;
import com.shopsphere.user.dto.UserResponse;
import com.shopsphere.user.dto.UserUpdateRequest;
import com.shopsphere.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * UserService Interface - Business logic for user profile operations
 *
 * Defines contract for user management operations.
 */
public interface UserService {

    /**
     * Get user profile by ID
     *
     * @param userId the user's UUID
     * @return Optional containing UserResponse if found
     */
    Optional<UserResponse> getUserProfile(UUID userId);

    /**
     * Get user profile by email
     *
     * @param email the user's email
     * @return Optional containing UserResponse if found
     */
    Optional<UserResponse> getUserProfileByEmail(String email);

    /**
     * Update user profile with authorization check
     *
     * Only allows users to update their own profile unless they have ADMIN role.
     * Updates only the provided fields (partial update).
     *
     * @param targetUserId the ID of the user to update
     * @param authenticatedUserId the ID of the authenticated user making the request
     * @param authenticatedUserEmail the email of the authenticated user making the request
     * @param updateRequest the update request containing fields to update
     * @return UserResponse with updated user details
     * @throws AccessDeniedException if user tries to update someone else's profile without ADMIN role
     * @throws RuntimeException if user not found
     */
    UserResponse updateUserProfile(
        UUID targetUserId,
        UUID authenticatedUserId,
        String authenticatedUserEmail,
        UserUpdateRequest updateRequest
    );

    /**
     * Get raw User entity by ID
     *
     * @param userId the user's UUID
     * @return Optional containing User if found
     */
    Optional<User> getUserById(UUID userId);

    /**
     * Convert User entity to UserResponse DTO
     *
     * @param user the User entity
     * @return UserResponse DTO
     */
    UserResponse convertToUserResponse(User user);

    /**
     * Convert User entity to UserResponse DTO with message
     *
     * @param user the User entity
     * @param message the message to include in response
     * @return UserResponse DTO with message
     */
    UserResponse convertToUserResponse(User user, String message);

    /**
     * Get all users with pagination (Admin only)
     *
     * Returns a paginated list of all users in the system.
     *
     * @param pageable the pagination information (page number, size, sorting)
     * @return Page containing UserResponse objects
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Update a user's roles (Admin only)
     *
     * Allows admin to change the roles assigned to a user.
     * The user must be updated with the new set of roles.
     *
     * @param userId the ID of the user to update
     * @param updateRoleRequest the request containing the new set of roles
     * @return UserResponse with updated roles
     * @throws RuntimeException if user not found
     */
    UserResponse updateUserRoles(UUID userId, UpdateUserRoleRequest updateRoleRequest);
}

