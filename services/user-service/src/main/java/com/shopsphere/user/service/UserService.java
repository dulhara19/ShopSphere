package com.shopsphere.user.service;

import com.shopsphere.user.dto.UpdateUserRoleRequest;
import com.shopsphere.user.dto.UserResponse;
import com.shopsphere.user.dto.UserUpdateRequest;
import com.shopsphere.user.dto.UserInternalDto;
import com.shopsphere.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * UserService Interface - Business logic for user profile operations
 */
public interface UserService {

    Optional<UserResponse> getUserProfile(UUID userId);

    Optional<UserResponse> getUserProfileByEmail(String email);

    UserResponse updateUserProfile(
        UUID targetUserId,
        UUID authenticatedUserId,
        String authenticatedUserEmail,
        UserUpdateRequest updateRequest
    );

    Optional<User> getUserById(UUID userId);

    UserResponse convertToUserResponse(User user);

    UserResponse convertToUserResponse(User user, String message);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUserRoles(UUID userId, UpdateUserRoleRequest updateRoleRequest);

    /**
     * Retrieve a lightweight internal representation of a user for service-to-service calls.
     * (Phase 4.2: Data Lookup)
     *
     * @param userId the user's UUID
     * @return UserInternalDto containing minimal user details
     */
    UserInternalDto getInternalUserById(UUID userId);
}
