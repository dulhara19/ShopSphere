package com.shopsphere.user.controller;

import com.shopsphere.user.dto.RegisterRequest;
import com.shopsphere.user.dto.RegisterResponse;
import com.shopsphere.user.dto.LoginRequest;
import com.shopsphere.user.dto.LoginResponse;
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
 * - POST /api/auth/login - User login (returns JWT tokens)
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
     * @param registerRequest the registration request
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

    /**
     * Authenticate a user and return JWT tokens.
     * <p>
     * Endpoint: POST /api/auth/login
     * <p>
     * Request body:
     * {
     * "email": "john.doe@example.com",
     * "password": "securePassword123"
     * }
     * <p>
     * Success Response (HTTP 200):
     * {
     * "accessToken": "eyJhbGc...",
     * "refreshToken": "eyJhbGc...",
     * "tokenType": "Bearer",
     * "email": "john.doe@example.com",
     * "firstName": "John",
     * "lastName": "Doe",
     * "roles": ["CUSTOMER"],
     * "expiresIn": 900000
     * }
     *
     * @param loginRequest the login credentials
     * @return LoginResponse with JWT tokens and user details
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest loginRequest
    ) {
        log.info("Received login request for email: {}", loginRequest.getEmail());

        try {
            // Call service to authenticate user and generate JWT tokens
            LoginResponse response = authService.login(loginRequest);

            log.info("User successfully authenticated: {}", loginRequest.getEmail());
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

        } catch (Exception e) {
            log.error("Login failed for email: {} - {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        }
    }
}





