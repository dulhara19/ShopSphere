package com.shopsphere.user.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT Authentication Filter
 *
 * Responsibilities:
 * - Extract JWT token from Authorization header
 * - Validate JWT token using JwtUtils
 * - Extract user information from token
 * - Set user authentication in SecurityContextHolder
 * - Allow requests to proceed if token is valid
 *
 * This filter runs once per request and is applied before
 * UsernamePasswordAuthenticationFilter to enable JWT-based authentication.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    /**
     * Constant for Authorization header
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Constant for Bearer token prefix
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Do filter internal - Extract and validate JWT token from request
     *
     * Process:
     * 1. Extract JWT token from Authorization header
     * 2. Validate JWT token using JwtUtils
     * 3. Extract username (email) from token
     * 4. Extract userId from token claims
     * 5. Create authentication token
     * 6. Set authentication in SecurityContext
     * 7. Allow request to proceed
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain filter chain for continuing the request
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Step 1: Extract JWT token from Authorization header
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            String jwt = extractJwtFromHeader(authHeader);

            // If no token found, continue to next filter
            if (jwt == null) {
                log.debug("No JWT token found in request");
                filterChain.doFilter(request, response);
                return;
            }

            // Step 2: Validate JWT token
            if (!jwtUtils.validateToken(jwt)) {
                log.warn("Invalid JWT token received");
                filterChain.doFilter(request, response);
                return;
            }

            // Step 3: Extract username (email) from token
            String username = jwtUtils.extractUsername(jwt);

            // Step 4: Extract userId from token claims
            String userId = jwtUtils.extractUserId(jwt);

            // Step 5: Create authentication token
            // Empty authorities list - can be populated from database if needed
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    new ArrayList<>() // Authorities - can be loaded from database
                );

            // Set request details for audit purposes
            authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // Step 6: Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.debug("JWT authentication set for user: {}", username);

        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            // Continue to next filter - let other security filters handle the error
        }

        // Step 7: Continue to next filter
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     *
     * Expected format: "Bearer <jwt_token>"
     *
     * @param authHeader Authorization header value
     * @return JWT token if present and valid format, null otherwise
     */
    private String extractJwtFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}

