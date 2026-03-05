package com.example.shipping_service.controller;

import com.example.shipping_service.model.Shipping;
import com.example.shipping_service.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    @Autowired
    private ShippingService service;

    @PostMapping("/create-label")
    public Shipping createLabel(@Valid @RequestBody Shipping shipping) {
        return service.createLabel(shipping);
    }
    @GetMapping("/track/{trackingNumber}")
    public Shipping getShippingByTrackingNumber(@PathVariable String trackingNumber) {
        return service.getShippingByTrackingNumber(trackingNumber);

    }
    @PutMapping("/update-status/{trackingNumber}")
    public Shipping updateStatus(@PathVariable String trackingNumber, @RequestParam String status) {
        return service.updateShippingStatus(trackingNumber, status);
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
