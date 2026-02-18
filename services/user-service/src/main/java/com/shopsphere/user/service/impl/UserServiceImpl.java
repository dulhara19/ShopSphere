package com.shopsphere.user.service.impl;

import com.shopsphere.user.dto.UpdateUserRoleRequest;
import com.shopsphere.user.dto.UserResponse;
import com.shopsphere.user.dto.UserUpdateRequest;
import com.shopsphere.user.exception.AccessDeniedException;
import com.shopsphere.user.model.Role;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.UserRepository;
import com.shopsphere.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * UserServiceImpl - Implementation of UserService
 *
 * Handles user profile business logic including retrieval, updates, and admin operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Get user profile by ID
     *
     * @param userId the user's UUID
     * @return Optional containing UserResponse if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserProfile(UUID userId) {
        log.debug("Fetching user profile for ID: {}", userId);
        return userRepository.findById(userId)
            .map(this::convertToUserResponse);
    }

    /**
     * Get user profile by email
     *
     * @param email the user's email
     * @return Optional containing UserResponse if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserProfileByEmail(String email) {
        log.debug("Fetching user profile for email: {}", email);
        return userRepository.findByEmail(email)
            .map(this::convertToUserResponse);
    }

    /**
     * Update user profile with authorization check
     *
     * Security Logic:
     * - If user is ADMIN, can update any user's profile
     * - If user is not ADMIN, can only update their own profile (IDs must match)
     * - If IDs don't match and user is not ADMIN, throw AccessDeniedException
     *
     * Update Logic:
     * - Only updates fields that are provided (not null) in the request
     * - Automatically updates the updatedAt timestamp
     * - Saves changes to database
     *
     * @param targetUserId the ID of the user to update
     * @param authenticatedUserId the ID of the authenticated user making the request
     * @param authenticatedUserEmail the email of the authenticated user making the request
     * @param updateRequest the update request containing fields to update
     * @return UserResponse with updated user details
     * @throws AccessDeniedException if user tries to update someone else's profile without ADMIN role
     * @throws RuntimeException if target user not found
     */
    @Override
    @Transactional
    public UserResponse updateUserProfile(
        UUID targetUserId,
        UUID authenticatedUserId,
        String authenticatedUserEmail,
        UserUpdateRequest updateRequest
    ) {
        log.info("Update profile request for user: {} from authenticated user: {}", targetUserId, authenticatedUserId);

        // Step 1: Get the user making the request
        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
            .orElseThrow(() -> {
                log.error("Authenticated user not found: {}", authenticatedUserEmail);
                return new RuntimeException("Authenticated user not found");
            });

        // Step 2: Check authorization
        // User can update their own profile OR if they have ADMIN role
        boolean isAdmin = authenticatedUser.hasRole(Role.ADMIN);
        boolean isOwnProfile = targetUserId.equals(authenticatedUserId);

        if (!isOwnProfile && !isAdmin) {
            log.warn("Access denied: User {} attempted to update profile of user {}", authenticatedUserId, targetUserId);
            throw new AccessDeniedException(
                "You do not have permission to update this user's profile. " +
                "Only the profile owner or administrators can make changes."
            );
        }

        // Step 3: Get the target user to update
        User targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> {
                log.error("Target user not found: {}", targetUserId);
                return new RuntimeException("User not found");
            });

        // Step 4: Update only provided fields (partial update)
        if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().isBlank()) {
            targetUser.setFirstName(updateRequest.getFirstName());
            log.debug("Updated firstName for user: {}", targetUserId);
        }

        if (updateRequest.getLastName() != null && !updateRequest.getLastName().isBlank()) {
            targetUser.setLastName(updateRequest.getLastName());
            log.debug("Updated lastName for user: {}", targetUserId);
        }

        if (updateRequest.getPhone() != null && !updateRequest.getPhone().isBlank()) {
            targetUser.setPhone(updateRequest.getPhone());
            log.debug("Updated phone for user: {}", targetUserId);
        }

        if (updateRequest.getAddress() != null && !updateRequest.getAddress().isBlank()) {
            targetUser.setAddress(updateRequest.getAddress());
            log.debug("Updated address for user: {}", targetUserId);
        }

        if (updateRequest.getCity() != null && !updateRequest.getCity().isBlank()) {
            targetUser.setCity(updateRequest.getCity());
            log.debug("Updated city for user: {}", targetUserId);
        }

        if (updateRequest.getState() != null && !updateRequest.getState().isBlank()) {
            targetUser.setState(updateRequest.getState());
            log.debug("Updated state for user: {}", targetUserId);
        }

        if (updateRequest.getPostalCode() != null && !updateRequest.getPostalCode().isBlank()) {
            targetUser.setPostalCode(updateRequest.getPostalCode());
            log.debug("Updated postalCode for user: {}", targetUserId);
        }

        if (updateRequest.getCountry() != null && !updateRequest.getCountry().isBlank()) {
            targetUser.setCountry(updateRequest.getCountry());
            log.debug("Updated country for user: {}", targetUserId);
        }

        // Step 5: Save updated user (updatedAt timestamp automatically updated by @UpdateTimestamp)
        User updatedUser = userRepository.save(targetUser);
        log.info("User profile updated successfully: {}", targetUserId);

        // Step 6: Convert and return updated user profile
        return convertToUserResponse(updatedUser, "Profile updated successfully");
    }

    /**
     * Get raw User entity by ID
     *
     * @param userId the user's UUID
     * @return Optional containing User if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(UUID userId) {
        log.debug("Fetching user entity for ID: {}", userId);
        return userRepository.findById(userId);
    }

    /**
     * Convert User entity to UserResponse DTO
     *
     * @param user the User entity
     * @return UserResponse DTO
     */
    @Override
    public UserResponse convertToUserResponse(User user) {
        return convertToUserResponse(user, null);
    }

    /**
     * Convert User entity to UserResponse DTO with message
     *
     * @param user the User entity
     * @param message the message to include in response
     * @return UserResponse DTO with message
     */
    @Override
    public UserResponse convertToUserResponse(User user, String message) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .username(user.getUsername())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phone(user.getPhone())
            .address(user.getAddress())
            .city(user.getCity())
            .state(user.getState())
            .postalCode(user.getPostalCode())
            .country(user.getCountry())
            .profilePictureUrl(user.getProfilePictureUrl())
            .roles(user.getRoles())
            .isEnabled(user.getIsEnabled())
            .isEmailVerified(user.getIsEmailVerified())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .message(message)
            .build();
    }

    /**
     * Get all users with pagination (Admin only)
     *
     * Returns a paginated list of all users in the system.
     * Each user is converted to a UserResponse DTO.
     *
     * @param pageable the pagination information (page number, size, sorting)
     * @return Page containing UserResponse objects
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable)
            .map(this::convertToUserResponse);
    }

    /**
     * Update a user's roles (Admin only)
     *
     * Allows admin to change the roles assigned to a user.
     * Replaces the user's current roles with the provided roles.
     *
     * @param userId the ID of the user to update
     * @param updateRoleRequest the request containing the new set of roles
     * @return UserResponse with updated roles
     * @throws RuntimeException if user not found
     */
    @Override
    @Transactional
    public UserResponse updateUserRoles(UUID userId, UpdateUserRoleRequest updateRoleRequest) {
        log.info("Admin updating roles for user: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("User not found for role update: {}", userId);
                return new RuntimeException("User not found");
            });

        // Update the user's roles
        user.setRoles(updateRoleRequest.getRoles());
        log.debug("Updated roles for user {}: {}", userId, updateRoleRequest.getRoles());

        // Save and return updated user
        User updatedUser = userRepository.save(user);
        log.info("User roles updated successfully: {}", userId);

        return convertToUserResponse(updatedUser, "User roles updated successfully");
    }
}


