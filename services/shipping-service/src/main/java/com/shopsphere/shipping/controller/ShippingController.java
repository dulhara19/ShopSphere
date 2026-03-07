package com.shopsphere.shipping.controller;

import com.shopsphere.shipping.model.Shipping;
import com.shopsphere.shipping.dto.*;
import com.shopsphere.shipping.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    @Autowired
    private ShippingService service;

    private ShippingDTO convertToDto(Shipping shipping) {
        if (shipping == null) return null;

        ShippingDTO dto = new ShippingDTO();
        dto.setId(shipping.getId());
        dto.setOrderId(shipping.getOrderId());
        dto.setTrackingNumber(shipping.getTrackingNumber());
        dto.setCarrier(shipping.getCarrier());
        dto.setServiceType(shipping.getServiceType());
        dto.setStatus(shipping.getStatus());
        dto.setShippingCost(shipping.getShippingCost());
        dto.setStreet(shipping.getStreet());
        dto.setCity(shipping.getCity());
        dto.setZipCode(shipping.getZipCode());
        dto.setCountry(shipping.getCountry());
        dto.setEstimatedDays(shipping.getEstimatedDays());
        dto.setCreatedAt(shipping.getCreatedAt());
        dto.setUpdatedAt(shipping.getUpdatedAt());
        return dto;
    }

    @PostMapping("/create-label")
    public ResponseEntity<ShippingDTO> createLabel(@Valid @RequestBody Shipping shipping) {
        Shipping savedShipping = service.createLabel(shipping);
        return ResponseEntity.ok(convertToDto(savedShipping));
    }

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<ShippingDTO> getShippingByTrackingNumber(@PathVariable String trackingNumber) {
        Shipping shipping = service.getShippingByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(convertToDto(shipping));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ShippingDTO>> getShipmentsByOrderId(@PathVariable String orderId) {
        List<ShippingDTO> shipments = service.getShipmentsByOrderId(orderId)
                .stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(shipments);
    }

    @PutMapping("/update-status/{trackingNumber}")
    public ResponseEntity<ShippingDTO> updateStatus(@PathVariable String trackingNumber, @RequestParam String status) {
        Shipping updated = service.updateShippingStatus(trackingNumber, status);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @GetMapping("/download-label/{trackingNumber}")
    public ResponseEntity<byte[]> downloadLabel(@PathVariable String trackingNumber) {
        byte[] pdfContent = service.generateLabelPdf(trackingNumber);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=label.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    // --- New endpoints ---

    @PostMapping("/validate-address")
    public ResponseEntity<ApiResponse<AddressValidationResponse>> validateAddress(@RequestBody AddressDTO address) {
        AddressValidationResponse result = service.validateAddress(address);
        return ResponseEntity.ok(new ApiResponse<>(result));
    }

    @GetMapping("/zones")
    public ResponseEntity<ApiResponse<List<ShippingZoneDTO>>> getAllZones() {
        List<ShippingZoneDTO> zones = service.getAllZones();
        return ResponseEntity.ok(new ApiResponse<>(zones));
    }

    @GetMapping("/zones/{country}")
    public ResponseEntity<ApiResponse<ShippingZoneDTO>> getZoneByCountry(@PathVariable String country) {
        ShippingZoneDTO zone = service.getZoneByCountry(country);
        if (zone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(null));
        }
        return ResponseEntity.ok(new ApiResponse<>(zone));
    }

    @PostMapping("/calculate-rate")
    public ResponseEntity<ApiResponse<List<CarrierRateDTO>>> calculateRate(@RequestBody CalculateRateRequest request) {
        List<CarrierRateDTO> rates = service.calculateRates(request);
        return ResponseEntity.ok(new ApiResponse<>(rates));
    }

    @GetMapping("/rates/flat")
    public ResponseEntity<ApiResponse<List<FlatRateDTO>>> getFlatRates() {
        List<FlatRateDTO> rates = service.getFlatRates();
        return ResponseEntity.ok(new ApiResponse<>(rates));
    }

    @GetMapping("/free-shipping-threshold")
    public ResponseEntity<ApiResponse<FreeShippingThresholdDTO>> getFreeShippingThreshold() {
        FreeShippingThresholdDTO threshold = service.getFreeShippingThreshold();
        return ResponseEntity.ok(new ApiResponse<>(threshold));
    }
}
