package com.shopsphere.user.controller;

import com.shopsphere.user.dto.UpdateUserRoleRequest;
import com.shopsphere.user.dto.UserResponse;
import com.shopsphere.user.dto.UserUpdateRequest;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.UserRepository;
import com.shopsphere.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Get the currently authenticated user's profile
     *
     * @return ResponseEntity containing UserResponse
     */
    @GetMapping("/api/users/me")
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER') or hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(mapToResponse(user));
    }

    /**
     * Update the authenticated user's profile
     *
     * @param id the user ID to update
     * @param updateRequest the update request with profile changes
     * @return ResponseEntity containing updated UserResponse
     */
    @PutMapping("/api/users/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER') or hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateProfile(
        @PathVariable UUID id,
        @Valid @RequestBody UserUpdateRequest updateRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authEmail = (String) authentication.getPrincipal();

        User authUser = userRepository.findByEmail(authEmail)
            .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        UserResponse response = userService.updateUserProfile(id, authUser.getId(), authEmail, updateRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all users with pagination (Admin only)
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return ResponseEntity containing paginated UserResponse
     */
    @GetMapping("/api/admin/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
        @PageableDefault(size = 20, page = 0) Pageable pageable) {
        log.info("Admin retrieving all users - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Update a user's roles (Admin only)
     *
     * @param id the user ID to update
     * @param updateRoleRequest the request containing new roles
     * @return ResponseEntity containing updated UserResponse
     */
    @PutMapping("/api/admin/users/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateUserRole(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUserRoleRequest updateRoleRequest) {
        log.info("Admin updating roles for user: {}", id);
        UserResponse response = userService.updateUserRoles(id, updateRoleRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Map User entity to UserResponse DTO
     *
     * @param user the User entity
     * @return UserResponse DTO
     */
    private UserResponse mapToResponse(User user) {
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
            .roles(user.getRoles())
            .isEnabled(user.getIsEnabled())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
