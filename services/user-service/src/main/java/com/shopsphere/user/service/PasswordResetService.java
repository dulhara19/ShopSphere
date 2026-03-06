package com.shopsphere.user.service;

import com.shopsphere.user.dto.PasswordResetEventDto;
import com.shopsphere.user.entity.PasswordResetToken;
import com.shopsphere.user.exception.UserNotFoundException;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.PasswordResetTokenRepository;
import com.shopsphere.user.repository.UserRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private static final long EXPIRATION_MINUTES = 15;
    private static final int TOKEN_LENGTH = 6;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserEventPublisher userEventPublisher;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.info("Password reset requested for non-existent email: {}", email);
            return;
        }

        String tokenValue = generateSixDigitToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        PasswordResetToken token = PasswordResetToken.builder()
            .token(tokenValue)
            .user(user)
            .expiryDate(expiryDate)
            .build();

        passwordResetTokenRepository.save(token);

        PasswordResetEventDto eventDto = PasswordResetEventDto.builder()
            .email(user.getEmail())
            .token(tokenValue)
            .expiresAt(expiryDate)
            .build();
        userEventPublisher.publishPasswordResetEvent(eventDto);

        log.info("Password reset token generated for user: {}", email);
    }

    private String generateSixDigitToken() {
        SecureRandom random = new SecureRandom();
        String tokenValue;
        int attempts = 0;

        do {
            tokenValue = String.format("%0" + TOKEN_LENGTH + "d", random.nextInt(1_000_000));
            attempts++;
        } while (passwordResetTokenRepository.findByToken(tokenValue).isPresent() && attempts < 5);

        return tokenValue;
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        PasswordResetToken token = passwordResetTokenRepository.findByTokenAndUserEmail(code, email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reset code"));

        if (token.isExpired()) {
            passwordResetTokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset code has expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(token);
        log.info("Password reset completed for user: {}", email);
    }
}
