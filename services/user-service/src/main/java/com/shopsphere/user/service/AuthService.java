package com.shopsphere.user.service;

import com.shopsphere.user.dto.LoginRequest;
import com.shopsphere.user.dto.LoginResponse;
import com.shopsphere.user.dto.RegisterRequest;
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

import java.util.UUID;

/**
 * AuthService - Handles authentication and authorization logic.
 *
 * Responsibilities:
 * - User registration with email validation and password hashing
 * - Password encoding and validation
 * - User credential verification
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Register a new user in the system.
     *
     * Flow:
     * 1. Check if email already exists
     * 2. Encode the password using BCrypt
     * 3. Create a new User entity with encoded password
     * 4. Save to database
     * 5. Return the created user
     *
     * @param registerRequest the registration request containing user details
     * @return the created User entity
     * @throws UserAlreadyExistsException if email already exists
     */
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        log.info("Attempting to register user with email: {}", registerRequest.getEmail());

        // Step 1: Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: Email {} already exists", registerRequest.getEmail());
            throw new UserAlreadyExistsException(
                String.format("User with email '%s' already exists", registerRequest.getEmail())
            );
        }

        // Step 2: Encode password using BCrypt
        String encodedPassword = bCryptPasswordEncoder.encode(registerRequest.getPassword());

        // Step 3 & 4: Create and save new user
        User user = User.builder()
            .id(UUID.randomUUID())
            .email(registerRequest.getEmail())
            .username(registerRequest.getEmail()) // Using email as username
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .passwordHash(encodedPassword)
            .roles(registerRequest.getRoles())
            .isEnabled(true)
            .isEmailVerified(false)
            .build();

        User savedUser = userRepository.save(user);
        log.info("User successfully registered with email: {}", savedUser.getEmail());

        return savedUser;
    }
    /**
     * Authenticate a user and return JWT tokens.
     *
     * Flow:
     * 1. Find user by email
     * 2. Verify password matches hashed password
     * 3. Generate access token (15 min expiration)
     * 4. Generate refresh token (7 days expiration)
     * 5. Return user details with tokens
     *
     * @param loginRequest the login request containing email and password
     * @return LoginResponse with JWT tokens and user details
     * @throws UserNotFoundException if user not found
     * @throws RuntimeException if password verification fails
     */
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

        // Step 3 & 4: Generate JWT tokens
        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), user.getId().toString());
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


