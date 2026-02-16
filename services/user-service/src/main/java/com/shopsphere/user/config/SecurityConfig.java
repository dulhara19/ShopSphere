package com.shopsphere.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security Configuration Class
 *
 * Responsibilities:
 * - Configure password encoding using BCrypt
 * - Define security-related beans for the application
 */
@Configuration
public class SecurityConfig {

    /**
     * BCryptPasswordEncoder Bean
     *
     * Strength: 12 (default is 10, but 12 provides better security with minimal performance impact)
     * This encoder uses an adaptive hash that includes salt and adapts to future computational power increases.
     *
     * @return BCryptPasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}

