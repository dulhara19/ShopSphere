package com.shopsphere.shipping.controller;

import com.shopsphere.shipping.model.Shipping;
import com.shopsphere.shipping.dto.ShippingDTO;
import com.shopsphere.shipping.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/shipping")
@CrossOrigin(origins = "http://localhost:3000") 
public class ShippingController {

    @Autowired
    private ShippingService service;

    private ShippingDTO convertToDto(Shipping shipping) {
        if (shipping == null) return null;
        
        ShippingDTO dto = new ShippingDTO();
        dto.setOrderId(shipping.getOrderId());
        dto.setTrackingNumber(shipping.getTrackingNumber());
        dto.setCarrier(shipping.getCarrier());
        dto.setStatus(shipping.getStatus());
        dto.setShippingCost(shipping.getShippingCost());
        dto.setStreet(shipping.getStreet());
        dto.setCity(shipping.getCity());
        dto.setZipCode(shipping.getZipCode());
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
}