package com.shopsphere.user.config;

import com.shopsphere.user.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Configuration
 *
 * Responsibilities:
 * - Configure JWT authentication filter
 * - Set up authorization rules
 * - Configure session management (stateless)
 * - Disable CSRF for API endpoints
 * - Configure HTTP security
 * - Enable method-level security with @PreAuthorize
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Create BCryptPasswordEncoder bean for password hashing
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure Spring Security filter chain
     *
     * Configuration:
     * 1. Disable CSRF for stateless API
     * 2. Add JWT filter before UsernamePasswordAuthenticationFilter
     * 3. Set session management to STATELESS
     * 4. Permit all requests to /api/auth/** (registration, login, etc.)
     * 5. Require authentication for all other API endpoints
     * 6. Allow actuator endpoints for monitoring
     *
     * @param http HttpSecurity builder
     * @return SecurityFilterChain configured filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API (using JWT instead)
            .csrf(AbstractHttpConfigurer::disable)

            // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            // Configure session management to STATELESS (no session cookies)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow all authentication endpoints without authentication
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/auth/**").permitAll()

                // Allow actuator endpoints for monitoring
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()

                // Require authentication for all other endpoints
                .anyRequest().authenticated()
            )

            // Enable HTTP Basic authentication for development/testing
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
