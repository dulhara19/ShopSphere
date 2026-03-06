package com.shopsphere.user.controller.internal;

import com.shopsphere.user.dto.UserInternalDto;
import com.shopsphere.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * InternalUserController - Phase 4.2: Data Lookup
 * Service-to-service communication for user data retrieval
 */
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {

    private final UserService userService;

    /**
     * GET /internal/users/{id}
     *
     * Retrieve user details for internal service calls (e.g., from Product, Order, or Inventory Service).
     * This endpoint is permitted for all callers (service-to-service communication).
     *
     * @param id User UUID
     * @return UserInternalDto with user details (id, username, email, firstName, lastName, phone, roles)
     * @throws ResponseStatusException 404 if user not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserInternalDto> getInternalUser(@PathVariable UUID id) {
        log.info("Internal Data Lookup: Fetching details for user ID: {}", id);
        try {
            UserInternalDto userDto = userService.getInternalUserById(id);
            log.debug("User found: {}", id);
            return ResponseEntity.ok(userDto);
        } catch (ResponseStatusException ex) {
            log.warn("User not found: {}", id);
            throw ex;
        } catch (Exception ex) {
            log.error("Error fetching user {}: {}", id, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching user", ex);
        }
    }
}

