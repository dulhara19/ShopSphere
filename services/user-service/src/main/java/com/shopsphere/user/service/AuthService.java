package com.shopsphere.user.service;

import com.shopsphere.user.dto.LoginRequest;
import com.shopsphere.user.dto.LoginResponse;
import com.shopsphere.user.dto.RegisterRequest;
import com.shopsphere.user.dto.UserInternalDto;
import com.shopsphere.user.exception.UserAlreadyExistsException;
import com.shopsphere.user.exception.UserNotFoundException;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.UserRepository;
import com.shopsphere.user.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;
    private final UserEventPublisher userEventPublisher;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        log.info("Attempting to register user with email: {}", registerRequest.getEmail());

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: Email {} already exists", registerRequest.getEmail());
            throw new UserAlreadyExistsException(
                String.format("User with email '%s' already exists", registerRequest.getEmail())
            );
        }

        String encodedPassword = bCryptPasswordEncoder.encode(registerRequest.getPassword());

        User user = User.builder()
            .id(UUID.randomUUID())
            .email(registerRequest.getEmail())
            .username(registerRequest.getEmail())
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .passwordHash(encodedPassword)
            .roles(registerRequest.getRoles())
            .isEnabled(true)
            .isEmailVerified(false)
            .build();

        User savedUser = userRepository.save(user);
        log.info("User successfully registered with email: {}", savedUser.getEmail());

        try {
            UserInternalDto userDto = UserInternalDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .roles(savedUser.getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList()))
                .build();
            userEventPublisher.publishUserCreatedEvent(userDto);
        } catch (Exception e) {
            log.error("Failed to publish user.created event: {}", e.getMessage());
        }

        return savedUser;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Attempting login for email: {}", loginRequest.getEmail());

        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        List<String> roleNames = user.getRoles().stream()
            .map(Enum::name)
            .collect(Collectors.toList());

        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), user.getId().toString(), roleNames);
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail(), user.getId().toString());

        Date accessTokenExpiration = jwtUtils.getExpirationDate(accessToken);
        long expiresIn = accessTokenExpiration != null ?
            accessTokenExpiration.getTime() - System.currentTimeMillis() : 0;

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .roles(user.getRoles())
            .expiresIn(expiresIn)
            .build();
    }

    @Transactional(readOnly = true)
    public LoginResponse refreshAccessToken(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String email = jwtUtils.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<String> roleNames = user.getRoles().stream()
            .map(Enum::name)
            .collect(Collectors.toList());

        String newAccessToken = jwtUtils.generateAccessToken(user.getEmail(), user.getId().toString(), roleNames);
        Date expiration = jwtUtils.getExpirationDate(newAccessToken);
        long expiresIn = expiration != null ? expiration.getTime() - System.currentTimeMillis() : 0;

        return LoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .email(user.getEmail())
            .expiresIn(expiresIn)
            .build();
    }

    /**
     * Logout user by blacklisting the access token in Redis.
     *
     * Debug Version: Logs current time, expiration, TTL for troubleshooting
     */
    public void logout(String token) {
        log.info("========== LOGOUT DEBUG START ==========");
        log.info("Attempting to logout and blacklist token");

        String actualToken = token;
        if (token != null && token.startsWith("Bearer ")) {
            actualToken = token.substring(7);
            log.info("Extracted token from Bearer prefix");
        }

        log.info("Token (first 50 chars): {}", actualToken != null ? actualToken.substring(0, Math.min(50, actualToken.length())) : "null");

        try {
            if (actualToken == null || !jwtUtils.validateToken(actualToken)) {
                log.warn("Logout failed: Invalid or expired token");
                log.info("========== LOGOUT DEBUG END (FAILED - INVALID TOKEN) ==========");
                return;
            }
            log.info("✓ Token validation passed");

            Date expirationDate = jwtUtils.getExpirationDate(actualToken);
            if (expirationDate == null) {
                log.warn("Logout failed: Could not extract expiration date");
                log.info("========== LOGOUT DEBUG END (FAILED - NO EXPIRATION DATE) ==========");
                return;
            }
            log.info("✓ Expiration date extracted: {}", expirationDate);

            long currentTimeMillis = System.currentTimeMillis();
            long expirationTimeMillis = expirationDate.getTime();
            long ttl = expirationTimeMillis - currentTimeMillis;

            // 🔴 DEBUG LOGGING - Key information
            log.info("--- TTL CALCULATION DEBUG ---");
            log.info("Current Time (ms):     {}", currentTimeMillis);
            log.info("Expiration Time (ms):  {}", expirationTimeMillis);
            log.info("TTL Calculated (ms):   {}", ttl);
            log.info("TTL in Seconds:        {}", ttl / 1000);
            log.info("TTL in Minutes:        {}", ttl / 1000 / 60);
            log.info("TTL > 0?               {}", ttl > 0);
            log.info("--- END TTL DEBUG ---");

            if (ttl > 0) {
                log.info("✓ TTL is positive, proceeding with blacklist");
                tokenBlacklistService.blacklistToken(actualToken, ttl);
                log.info("✓ Token blacklisted successfully. TTL: {} ms ({} seconds)", ttl, ttl / 1000);
            } else {
                log.warn("⚠ TTL is <= 0 (token already expired or expires very soon). Not blacklisting.");
                log.info("Time until expiration: {} ms", ttl);
            }

            log.info("========== LOGOUT DEBUG END (SUCCESS) ==========");

        } catch (Exception e) {
            log.error("❌ Unexpected error during logout: {}", e.getMessage(), e);
            log.info("========== LOGOUT DEBUG END (FAILED - EXCEPTION) ==========");
        }
    }
}
