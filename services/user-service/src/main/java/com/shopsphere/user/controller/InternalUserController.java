package com.shopsphere.user.controller;

import com.shopsphere.user.dto.UserInternalDto;
import com.shopsphere.user.service.UserService;
import com.shopsphere.user.security.JwtUtils;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * InternalUserController - Service-to-service communication
 * (Phase 4.2: Data Lookup)
 */
@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * Get user details for internal service calls (e.g., from Product or Order Service)
     * * @param id User UUID
     * @return UserInternalDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserInternalDto> getInternalUser(@PathVariable UUID id) {
        log.info("Internal Data Lookup: Fetching details for user ID: {}", id);
        UserInternalDto userDto = userService.getInternalUserById(id);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Validate a JWT and return the internal user DTO.
     * Accepts JSON: { "token": "<jwt or Bearer ...>" }
     * Returns 200 with UserInternalDto on success, 401 on invalid/expired token.
     */
    @PostMapping("/validate")
    public ResponseEntity<UserInternalDto> validateToken(@RequestBody TokenValidationRequest request) {
        try {
            if (request == null || request.getToken() == null || request.getToken().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "token is required in request body");
            }

            UserInternalDto dto = jwtUtils.validateTokenAndGetUserInternalDto(request.getToken());
            return ResponseEntity.ok(dto);
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Token validation failed: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token", ex);
        }
    }

    // Simple request DTO as inner class to avoid creating a separate file
    public static class TokenValidationRequest {
        private String token;

        public TokenValidationRequest() {}

        public TokenValidationRequest(String token) { this.token = token; }

        public String getToken() { return token; }

        public void setToken(String token) { this.token = token; }
    }
}
