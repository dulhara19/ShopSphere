package com.shopsphere.order.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class ServiceAuthenticationFilter extends OncePerRequestFilter {

    private static final String SERVICE_NAME_HEADER = "X-Service-Name";
    private static final String SERVICE_SECRET_HEADER = "X-Service-Secret";
    private static final Set<String> ALLOWED_SERVICES = Set.of(
        "payment-service",
        "shipping-service",
        "inventory-service",
        "user-service",
        "product-service",
        "notification-service"
    );

    @Value("${app.service.secret}")
    private String masterSecret;

    @Value("${app.service.secrets.payment:#{null}}")
    private String paymentServiceSecret;

    @Value("${app.service.secrets.shipping:#{null}}")
    private String shippingServiceSecret;

    @Value("${app.service.secrets.inventory:#{null}}")
    private String inventoryServiceSecret;

    private Map<String, String> serviceSecrets;

    @PostConstruct
    public void init() {
        serviceSecrets = new HashMap<>();
        // Per-service secrets (if configured) take precedence over master secret
        if (paymentServiceSecret != null) {
            serviceSecrets.put("payment-service", paymentServiceSecret);
        }
        if (shippingServiceSecret != null) {
            serviceSecrets.put("shipping-service", shippingServiceSecret);
        }
        if (inventoryServiceSecret != null) {
            serviceSecrets.put("inventory-service", inventoryServiceSecret);
        }
        log.info("Service authentication initialized with {} per-service secrets configured", serviceSecrets.size());
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String serviceName = request.getHeader(SERVICE_NAME_HEADER);
        String secret = request.getHeader(SERVICE_SECRET_HEADER);

        if (StringUtils.hasText(serviceName) && StringUtils.hasText(secret)) {
            // Validate service name is in allowed list
            if (!ALLOWED_SERVICES.contains(serviceName)) {
                log.warn("Unknown service attempted authentication: {}", serviceName);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Check per-service secret first, then fall back to master secret
            String expectedSecret = serviceSecrets.getOrDefault(serviceName, masterSecret);

            if (secureEquals(expectedSecret, secret)) {
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        "service:" + serviceName,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_SERVICE"))
                    );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authenticated service: {}", serviceName);
            } else {
                log.warn("Invalid service secret from service: {} (IP: {})",
                    serviceName, request.getRemoteAddr());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean secureEquals(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(expected.getBytes(), actual.getBytes());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().startsWith("/internal/");
    }
}
