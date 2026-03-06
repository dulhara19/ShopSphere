package com.shopsphere.user.service;

import com.shopsphere.user.entity.PasswordResetToken;
import com.shopsphere.user.exception.UserNotFoundException;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.PasswordResetTokenRepository;
import com.shopsphere.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserEventPublisher userEventPublisher;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void resetPassword_throwsWhenUserNotFound() {
        String email = "missing@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> passwordResetService.resetPassword(email, "123456", "NewPass123!"));
    }

    @Test
    void resetPassword_throwsWhenCodeInvalid() {
        String email = "user@example.com";
        User user = User.builder()
            .id(UUID.randomUUID())
            .email(email)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByTokenAndUserEmail("123456", email))
            .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> passwordResetService.resetPassword(email, "123456", "NewPass123!"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void resetPassword_deletesExpiredTokenAndThrows() {
        String email = "user@example.com";
        User user = User.builder()
            .id(UUID.randomUUID())
            .email(email)
            .build();

        PasswordResetToken token = PasswordResetToken.builder()
            .id(UUID.randomUUID())
            .token("123456")
            .user(user)
            .expiryDate(LocalDateTime.now().minusMinutes(1))
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByTokenAndUserEmail("123456", email))
            .thenReturn(Optional.of(token));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> passwordResetService.resetPassword(email, "123456", "NewPass123!"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(passwordResetTokenRepository).delete(token);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_updatesPasswordAndDeletesToken() {
        String email = "user@example.com";
        String newPassword = "NewPass123!";
        User user = User.builder()
            .id(UUID.randomUUID())
            .email(email)
            .passwordHash("old")
            .build();

        PasswordResetToken token = PasswordResetToken.builder()
            .id(UUID.randomUUID())
            .token("123456")
            .user(user)
            .expiryDate(LocalDateTime.now().plusMinutes(10))
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByTokenAndUserEmail("123456", email))
            .thenReturn(Optional.of(token));
        when(passwordEncoder.encode(newPassword)).thenReturn("hashed");

        passwordResetService.resetPassword(email, "123456", newPassword);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser);
        assertEquals("hashed", savedUser.getPasswordHash());

        verify(passwordResetTokenRepository).delete(eq(token));
    }
}

