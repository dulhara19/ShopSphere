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

        // Phase 4.3: Publish user.created event
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
            log.error("Failed to publish user.created event, but user was saved: {}", e.getMessage());
            // Do not fail the registration if event publishing fails
        }

        return savedUser;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Attempting login for email: {}", loginRequest.getEmail());

        // Step 1: Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> {
                log.warn("Login failed: User not found with email {}", loginRequest.getEmail());
                return new UserNotFoundException(
                    String.format("User with email '%s' not found", loginRequest.getEmail())
                );
            });

        // Step 2: Verify password
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed: Invalid password for email {}", loginRequest.getEmail());
            throw new RuntimeException("Invalid email or password");
        }

        // 🔥 Step 2.5:
        List<String> roleNames = user.getRoles().stream()
            .map(Enum::name)
            .collect(Collectors.toList());

        // Step 3 & 4: Generate JWT tokens
        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), user.getId().toString(), roleNames);
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail(), user.getId().toString());

        Date accessTokenExpiration = jwtUtils.getExpirationDate(accessToken);
        long expiresIn = accessTokenExpiration != null ?
            accessTokenExpiration.getTime() - System.currentTimeMillis() : 0;

        log.info("User successfully authenticated: {}", user.getEmail());

        // Step 5: Return response with tokens
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
}
