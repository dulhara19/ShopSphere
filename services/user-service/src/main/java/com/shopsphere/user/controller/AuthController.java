package com.shopsphere.user.controller;

import com.shopsphere.user.dto.RegisterRequest;
import com.shopsphere.user.dto.RegisterResponse;
import com.shopsphere.user.model.User;
import com.shopsphere.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * AuthController - REST endpoints for authentication.
 *
 * Endpoints:
 * - POST /api/auth/register - User registration
 * - POST /api/auth/login - User login (future implementation)
 * - POST /api/auth/refresh - Token refresh (future implementation)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user.
     *
     * Endpoint: POST /api/auth/register
     *
     * Request body:
     * {
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "email": "john.doe@example.com",
     *   "password": "securePassword123",
     *   "roles": ["CUSTOMER"]
     * }
     *
     * @param registerRequest the registration request
     * @param bindingResult validation results
     * @return RegisterResponse with user details and success message
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
        @Valid @RequestBody RegisterRequest registerRequest
    ) {
        log.info("Received registration request for email: {}", registerRequest.getEmail());

        // Call service to register user
        User savedUser = authService.registerUser(registerRequest);

        // Build and return response
        RegisterResponse response = RegisterResponse.builder()
            .userId(savedUser.getId())
            .email(savedUser.getEmail())
            .firstName(savedUser.getFirstName())
            .lastName(savedUser.getLastName())
            .message("User registered successfully")
            .build();

        log.info("User registered successfully: {}", savedUser.getId());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }
}





