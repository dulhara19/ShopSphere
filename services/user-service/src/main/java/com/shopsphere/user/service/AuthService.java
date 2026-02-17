package com.shopsphere.user.service;

import com.shopsphere.user.dto.RegisterRequest;
import com.shopsphere.user.exception.UserAlreadyExistsException;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}


