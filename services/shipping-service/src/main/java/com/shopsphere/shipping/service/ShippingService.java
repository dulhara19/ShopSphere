package com.shopsphere.shipping.service;

import com.shopsphere.shipping.model.Shipping;
import com.shopsphere.shipping.repository.ShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShippingService {

    @Autowired
    private ShippingRepository repository;

    public Shipping createLabel(Shipping shipping) {
        double basePrice = 200.0;
        double weightCharge = 0;
        double distanceCharge = 0;

        String carrierName = shipping.getCarrier().trim().toUpperCase();

        switch (carrierName) {
            case "FEDEX":
                weightCharge = shipping.getWeight() * 60.0;
                distanceCharge = shipping.getDistance() * 12.0;
                shipping.setEstimatedDays(5);
                break;
            case "DHL":
                weightCharge = shipping.getWeight() * 55.0;
                distanceCharge = shipping.getDistance() * 15.0;
                shipping.setEstimatedDays(7);
                break;
            case "UPS":
                weightCharge = shipping.getWeight() * 50.0;
                distanceCharge = shipping.getDistance() * 10.0;
                shipping.setEstimatedDays(4);
                break;
            default:
                weightCharge = shipping.getWeight() * 40.0;
                distanceCharge = shipping.getDistance() * 8.0;
                shipping.setEstimatedDays(10);
        }

        double totalCost = basePrice + weightCharge + distanceCharge;
        shipping.setShippingCost(totalCost);
        shipping.setTrackingNumber("SHP-" + System.currentTimeMillis());
        shipping.setStatus("PENDING");
        shipping.setCreatedAt(LocalDateTime.now());
        shipping.setUpdatedAt(LocalDateTime.now());

        return repository.save(shipping);
    }

    public Shipping getShippingByTrackingNumber(String trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Shipping label not found for tracking number: " + trackingNumber));
    }

    public List<Shipping> getShipmentsByOrderId(String orderId) {
        return repository.findByOrderId(orderId);
    }

    public Shipping updateShippingStatus(String trackingNumber, String newStatus) {
        Shipping shipping = repository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Shipping label not found"));

        shipping.setStatus(newStatus);
        shipping.setUpdatedAt(LocalDateTime.now());
        return repository.save(shipping);
    }

    public byte[] generateLabelPdf(String trackingNumber) {
        Shipping shipping = repository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Shipping label not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("SHIPPING LABEL").setBold().setFontSize(20));
        document.add(new Paragraph("Tracking Number: " + shipping.getTrackingNumber()));
        document.add(new Paragraph("Carrier: " + shipping.getCarrier()));
        document.add(new Paragraph("Order ID: " + shipping.getOrderId()));
        document.add(new Paragraph("Address: " + shipping.getStreet() + ", " + shipping.getCity()));
        document.add(new Paragraph("Zip Code: " + shipping.getZipCode()));
        document.add(new Paragraph("Cost: Rs. " + shipping.getShippingCost()));

        document.close();
        return out.toByteArray();
    }
}
