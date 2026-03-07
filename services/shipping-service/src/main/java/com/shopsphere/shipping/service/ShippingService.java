package com.shopsphere.shipping.service;

import com.shopsphere.shipping.model.Shipping;
import com.shopsphere.shipping.repository.ShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import com.shopsphere.shipping.dto.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    // --- Address Validation ---

    public AddressValidationResponse validateAddress(AddressDTO address) {
        AddressValidationResponse response = new AddressValidationResponse();
        List<String> errors = new ArrayList<>();

        if (address.getStreet() == null || address.getStreet().trim().isEmpty()) {
            errors.add("Street is required");
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            errors.add("City is required");
        }
        if (address.getPostalCode() == null || address.getPostalCode().trim().isEmpty()) {
            errors.add("Postal code is required");
        }
        if (address.getCountry() == null || address.getCountry().trim().isEmpty()) {
            errors.add("Country is required");
        }

        response.setErrors(errors);
        response.setSuggestions(new ArrayList<>());

        if (errors.isEmpty()) {
            response.setValid(true);
            AddressDTO standardized = new AddressDTO();
            standardized.setStreet(address.getStreet().trim());
            standardized.setCity(address.getCity().trim());
            standardized.setState(address.getState() != null ? address.getState().trim() : "");
            standardized.setPostalCode(address.getPostalCode().trim());
            standardized.setCountry(address.getCountry().trim().toUpperCase());
            response.setStandardizedAddress(standardized);
        } else {
            response.setValid(false);
            response.setStandardizedAddress(null);
        }

        return response;
    }

    // --- Shipping Zones ---

    public List<ShippingZoneDTO> getAllZones() {
        List<ShippingZoneDTO> zones = new ArrayList<>();

        ShippingZoneDTO domestic = new ShippingZoneDTO();
        domestic.setId("zone-domestic");
        domestic.setName("Domestic");
        domestic.setCountries(Arrays.asList("LK"));
        domestic.setRates(Arrays.asList(
                new ZoneRateDTO("FEDEX", "Standard", 5.00, 1.50, 3),
                new ZoneRateDTO("DHL", "Standard", 4.50, 1.20, 4),
                new ZoneRateDTO("UPS", "Standard", 4.00, 1.00, 5)
        ));
        zones.add(domestic);

        ShippingZoneDTO southAsia = new ShippingZoneDTO();
        southAsia.setId("zone-south-asia");
        southAsia.setName("South Asia");
        southAsia.setCountries(Arrays.asList("IN", "BD", "NP", "PK"));
        southAsia.setRates(Arrays.asList(
                new ZoneRateDTO("FEDEX", "International Economy", 12.00, 3.00, 7),
                new ZoneRateDTO("DHL", "International Economy", 11.00, 2.80, 8),
                new ZoneRateDTO("UPS", "International Economy", 10.00, 2.50, 9)
        ));
        zones.add(southAsia);

        ShippingZoneDTO international = new ShippingZoneDTO();
        international.setId("zone-international");
        international.setName("International");
        international.setCountries(Arrays.asList("US", "GB", "AU", "CA", "DE"));
        international.setRates(Arrays.asList(
                new ZoneRateDTO("FEDEX", "International Priority", 25.00, 6.00, 5),
                new ZoneRateDTO("DHL", "International Express", 22.00, 5.50, 6),
                new ZoneRateDTO("UPS", "Worldwide Express", 20.00, 5.00, 7)
        ));
        zones.add(international);

        return zones;
    }

    public ShippingZoneDTO getZoneByCountry(String countryCode) {
        String code = countryCode.trim().toUpperCase();
        List<ShippingZoneDTO> zones = getAllZones();
        for (ShippingZoneDTO zone : zones) {
            if (zone.getCountries().contains(code)) {
                return zone;
            }
        }
        return null;
    }

    // --- Calculate Rates ---

    public List<CarrierRateDTO> calculateRates(CalculateRateRequest request) {
        double totalWeight = 0.0;
        if (request.getItems() != null) {
            for (CalculateRateRequest.RateItem item : request.getItems()) {
                totalWeight += item.getWeight();
            }
        }
        if (totalWeight <= 0) {
            totalWeight = 1.0;
        }

        List<CarrierRateDTO> rates = new ArrayList<>();

        // FEDEX
        CarrierRateDTO fedex = new CarrierRateDTO();
        fedex.setCarrier("FEDEX");
        fedex.setService("International Priority");
        fedex.setRate(Math.round((200.0 + totalWeight * 60.0) * 100.0) / 100.0);
        fedex.setCurrency("USD");
        fedex.setEstimatedDays(5);
        fedex.setDeliveryDate(LocalDate.now().plusDays(5).toString());
        rates.add(fedex);

        // DHL
        CarrierRateDTO dhl = new CarrierRateDTO();
        dhl.setCarrier("DHL");
        dhl.setService("Express Worldwide");
        dhl.setRate(Math.round((200.0 + totalWeight * 55.0) * 100.0) / 100.0);
        dhl.setCurrency("USD");
        dhl.setEstimatedDays(7);
        dhl.setDeliveryDate(LocalDate.now().plusDays(7).toString());
        rates.add(dhl);

        // UPS
        CarrierRateDTO ups = new CarrierRateDTO();
        ups.setCarrier("UPS");
        ups.setService("Worldwide Saver");
        ups.setRate(Math.round((200.0 + totalWeight * 50.0) * 100.0) / 100.0);
        ups.setCurrency("USD");
        ups.setEstimatedDays(4);
        ups.setDeliveryDate(LocalDate.now().plusDays(4).toString());
        rates.add(ups);

        // Standard
        CarrierRateDTO standard = new CarrierRateDTO();
        standard.setCarrier("Standard");
        standard.setService("Standard Shipping");
        standard.setRate(Math.round((200.0 + totalWeight * 40.0) * 100.0) / 100.0);
        standard.setCurrency("USD");
        standard.setEstimatedDays(10);
        standard.setDeliveryDate(LocalDate.now().plusDays(10).toString());
        rates.add(standard);

        return rates;
    }

    // --- Flat Rates ---

    public List<FlatRateDTO> getFlatRates() {
        List<FlatRateDTO> rates = new ArrayList<>();
        rates.add(new FlatRateDTO("Standard", "7-10 days", 5.99, "USD"));
        rates.add(new FlatRateDTO("Express", "3-5 days", 12.99, "USD"));
        rates.add(new FlatRateDTO("Overnight", "1-2 days", 24.99, "USD"));
        return rates;
    }

    // --- Free Shipping Threshold ---

    public FreeShippingThresholdDTO getFreeShippingThreshold() {
        return new FreeShippingThresholdDTO(true, 50.00, "USD");
    }
}
