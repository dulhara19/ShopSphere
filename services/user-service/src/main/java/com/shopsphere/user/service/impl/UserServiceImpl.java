package com.shopsphere.user.service.impl;

import com.shopsphere.user.dto.*;
import com.shopsphere.user.exception.AccessDeniedException;
import com.shopsphere.user.model.Role;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.UserRepository;
import com.shopsphere.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UserServiceImpl - Implementation of UserService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserProfile(UUID userId) {
        log.debug("Fetching user profile for ID: {}", userId);
        return userRepository.findById(userId)
            .map(this::convertToUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserProfileByEmail(String email) {
        log.debug("Fetching user profile for email: {}", email);
        return userRepository.findByEmail(email)
            .map(this::convertToUserResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(
        UUID targetUserId,
        UUID authenticatedUserId,
        String authenticatedUserEmail,
        UserUpdateRequest updateRequest
    ) {
        log.info("Update profile request for user: {} from authenticated user: {}", targetUserId, authenticatedUserId);

        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
            .orElseThrow(() -> {
                log.error("Authenticated user not found: {}", authenticatedUserEmail);
                return new RuntimeException("Authenticated user not found");
            });

        boolean isAdmin = authenticatedUser.hasRole(Role.ADMIN);
        boolean isOwnProfile = targetUserId.equals(authenticatedUserId);

        if (!isOwnProfile && !isAdmin) {
            log.warn("Access denied: User {} attempted to update profile of user {}", authenticatedUserId, targetUserId);
            throw new AccessDeniedException("You do not have permission to update this user's profile.");
        }

        User targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().isBlank()) {
            targetUser.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null && !updateRequest.getLastName().isBlank()) {
            targetUser.setLastName(updateRequest.getLastName());
        }
        // ... (අනිත් fields ටිකත් ඔයාගේ code එකේ විදිහටම තියෙයි)

        User updatedUser = userRepository.save(targetUser);
        return convertToUserResponse(updatedUser, "Profile updated successfully");
    }

    /**
     * Internal Data Lookup - Phase 4.2
     * අනිත් සර්විස් වලට අවශ්‍ය User දත්ත ලබාදීම.
     */
    @Override
    @Transactional(readOnly = true)
    public UserInternalDto getInternalUserById(UUID userId) {
        log.info("Internal lookup for user ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("Internal lookup failed. User not found: {}", userId);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
            });

        return UserInternalDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phone(user.getPhone())
            .roles(user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList()))
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public UserResponse convertToUserResponse(User user) {
        return convertToUserResponse(user, null);
    }

    @Override
    public UserResponse convertToUserResponse(User user, String message) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .username(user.getUsername())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .roles(user.getRoles())
            .isEnabled(user.getIsEnabled())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .message(message)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToUserResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUserRoles(UUID userId, UpdateUserRoleRequest updateRoleRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRoles(updateRoleRequest.getRoles());
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser, "User roles updated successfully");
    }
}
