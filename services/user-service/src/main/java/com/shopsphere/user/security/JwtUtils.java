package com.shopsphere.user.security;
import io.jsonwebtoken.Claims;
import java.util.List;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Utility Class
 *
 * Responsibilities:
 * - Generate JWT access tokens (15 minutes expiration)
 * - Generate JWT refresh tokens (7 days expiration)
 * - Validate JWT tokens
 * - Extract claims from JWT tokens
 * - Handle JWT signing and verification
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // 900000 ms = 15 minutes

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpiration; // 604800000 ms = 7 days

    /**
     * Generate a secret key from the JWT secret string
     *
     * @return SecretKey for JWT signing and verification
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate a JWT Access Token (short-lived, 15 minutes)
     *
     * @param username the username/email of the user
     * @param userId the user ID
     * @return JWT token string
     */
    public String generateAccessToken(String username, String userId, List<String> roles) {
        return generateToken(username, userId, roles, jwtExpiration, "access");
    }

    /**
     * Generate a JWT Refresh Token (long-lived, 7 days)
     *
     * @param username the username/email of the user
     * @param userId the user ID
     * @return JWT token string
     */
    public String generateRefreshToken(String username, String userId) {
        return generateToken(username, userId, null, jwtRefreshExpiration, "refresh");
    }

    /**
     * Generate a JWT token with custom claims
     *
     * @param username the username/email of the user
     * @param userId the user ID
     * @param expirationTime expiration time in milliseconds
     * @param tokenType type of token (access/refresh)
     * @return JWT token string
     */
    private String generateToken(String username, String userId, List<String> roles, long expirationTime, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tokenType", tokenType);
        claims.put("roles", roles);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        try {
            return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token: {}", e.getMessage());
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Validate a JWT token
     *
     * @param token the JWT token string to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extract username (subject) from JWT token
     *
     * @param token the JWT token string
     * @return username extracted from token subject
     */
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract user ID from JWT token
     *
     * @param token the JWT token string
     * @return user ID extracted from token claims
     */
    public String extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("userId", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract token type from JWT token
     *
     * @param token the JWT token string
     * @return token type (access/refresh)
     */
    public String extractTokenType(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("tokenType", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting token type from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get expiration time from JWT token
     *
     * @param token the JWT token string
     * @return expiration date of the token
     */
    public Date getExpirationDate(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting expiration date from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if a token is expired
     *
     * @param token the JWT token string
     * @return true if token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDate(token);
        if (expiration == null) {
            return true;
        }
        return expiration.before(new Date());
    }

    /**
     * Get all claims from a JWT token
     *
     * @param token the JWT token string
     * @return Claims object containing all token claims
     */
    public Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            return null;
        }
    }
    /**
     * Extract roles from the JWT token
     *
     * @param token JWT token
     * @return List of roles
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        try {
            Claims claims = getAllClaims(token);
            List<String> roles = claims.get("roles", List.class);

            return roles != null ? roles : new java.util.ArrayList<>();
        } catch (Exception e) {
            log.error("Error extracting roles from token: {}", e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

}


