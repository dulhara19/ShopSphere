package com.shopsphere.user.controller;

import com.shopsphere.user.dto.AddressDto;
import com.shopsphere.user.model.Address;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.UserRepository;
import com.shopsphere.user.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AddressController - REST endpoints for address management
 *
 * Provides endpoints for users to manage their addresses:
 * - Create new addresses
 * - Retrieve all addresses
 * - Set default address
 * - Delete addresses
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/me/addresses")
public class AddressController {

    private final AddressService addressService;
    private final UserRepository userRepository;

    /**
     * Add a new address for the authenticated user
     *
     * @param addressDto the address data to create
     * @return ResponseEntity containing the created AddressDto
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER') or hasAuthority('ADMIN')")
    public ResponseEntity<AddressDto> addAddress(@Valid @RequestBody AddressDto addressDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User {} is adding a new address", user.getId());

        Address createdAddress = addressService.addAddress(user, addressDto);
        AddressDto responseDto = addressService.convertToDto(createdAddress);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * Retrieve all addresses for the authenticated user
     *
     * @return ResponseEntity containing list of AddressDto
     */
    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER') or hasAuthority('ADMIN')")
    public ResponseEntity<List<AddressDto>> getAllAddresses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User {} is retrieving all addresses", user.getId());

        List<Address> addresses = addressService.getAddressesByUser(user);
        List<AddressDto> addressDtos = addresses.stream()
            .map(addressService::convertToDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(addressDtos);
    }

    /**
     * Set an address as the default address for the authenticated user
     *
     * @param id the address ID to set as default
     * @return ResponseEntity containing the updated AddressDto
     */
    @PutMapping("/{id}/default")
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER') or hasAuthority('ADMIN')")
    public ResponseEntity<AddressDto> setDefaultAddress(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User {} is setting address {} as default", user.getId(), id);

        Address updatedAddress = addressService.setDefaultAddress(user, id);
        AddressDto responseDto = addressService.convertToDto(updatedAddress);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * Delete an address for the authenticated user
     *
     * @param id the address ID to delete
     * @return ResponseEntity with HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User {} is deleting address {}", user.getId(), id);

        addressService.deleteAddress(user, id);

        return ResponseEntity.noContent().build();
    }
}

