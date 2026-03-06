package com.shopsphere.user.controller.internal;

import com.shopsphere.user.dto.AuthValidationResponse;
import com.shopsphere.user.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
@Slf4j
public class InternalAuthController {

    private final JwtUtils jwtUtils;

    @PostMapping("/validate")
    public ResponseEntity<AuthValidationResponse> validate(@RequestParam("token") String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "token is required");
        }

        boolean isValid = jwtUtils.validateToken(token);
        if (!isValid) {
            return ResponseEntity.ok(AuthValidationResponse.builder()
                    .isAuthenticated(false)
                    .build());
        }

        String userId = jwtUtils.extractUserId(token);
        String email = jwtUtils.extractUsername(token);
        List<String> roleList = jwtUtils.extractRoles(token);
        Set<String> roles = roleList == null ? new HashSet<>() : new HashSet<>(roleList);

        return ResponseEntity.ok(AuthValidationResponse.builder()
                .isAuthenticated(true)
                .userId(userId)
                .email(email)
                .roles(roles)
                .build());
    }
}

