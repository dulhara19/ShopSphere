package com.shopsphere.user.controller;

import com.shopsphere.user.dto.RegisterRequest;
import com.shopsphere.user.dto.RegisterResponse;
import com.shopsphere.user.dto.LoginRequest;
import com.shopsphere.user.dto.LoginResponse;
import com.shopsphere.user.dto.RefreshTokenRequest;
import com.shopsphere.user.dto.ForgotPasswordRequest;
import com.shopsphere.user.dto.ForgotPasswordResponse;
import com.shopsphere.user.dto.ResetPasswordRequest;
import com.shopsphere.user.model.User;
import com.shopsphere.user.service.AuthService;
import com.shopsphere.user.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Authentication and Password Management APIs")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account in the system.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
        @Valid @RequestBody RegisterRequest registerRequest
    ) {
        log.info("Received registration request for email: {}", registerRequest.getEmail());

        User savedUser = authService.registerUser(registerRequest);

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

    @Operation(
        summary = "User Login",
        description = "Authenticates user and returns JWT access and refresh tokens.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest loginRequest
    ) {
        log.info("Received login request for email: {}", loginRequest.getEmail());

        try {
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

    @Operation(
        summary = "Refresh Access Token",
        description = "Generates a new access token using a valid refresh token.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "New access token generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or missing refresh token"),
        @ApiResponse(responseCode = "401", description = "Refresh token expired or invalid")
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
        @Valid @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        log.info("Received token refresh request");

        try {
            LoginResponse response = authService.refreshAccessToken(refreshTokenRequest.getRefreshToken());

            log.info("Access token refreshed successfully");
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(
        summary = "User Logout",
        description = "Logs out the user by blacklisting the current access token in Redis.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully logged out"),
        @ApiResponse(responseCode = "400", description = "Invalid or missing token"),
        @ApiResponse(responseCode = "401", description = "Token expired or invalid")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        log.info("Received logout request");

        try {
            // Extract token from Authorization header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("Logout failed: Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

            // Blacklist the token
            authService.logout(token);

            log.info("User logged out successfully");
            return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();

        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(
        summary = "Forgot Password",
        description = "Triggers a password reset email/code for the given email address.",
        security = {}
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Received forgot-password request for email: {}", request.getEmail());
        passwordResetService.generatePasswordResetToken(request.getEmail());

        ForgotPasswordResponse response = ForgotPasswordResponse.builder()
            .message("If the email exists, a reset code has been sent")
            .build();
        return ResponseEntity.accepted().body(response);
    }

    @Operation(
        summary = "Reset Password",
        description = "Resets the user password using the code received via email.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Password reset successful"),
        @ApiResponse(responseCode = "400", description = "Invalid code or expired token")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Received reset-password request for email: {}", request.getEmail());
        passwordResetService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
