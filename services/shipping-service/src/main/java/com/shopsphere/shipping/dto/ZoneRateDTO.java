package com.shopsphere.shipping.dto;

public class ZoneRateDTO {
    private String carrier;
    private String service;
    private double baseRate;
    private double perKgRate;
    private int estimatedDays;

    public ZoneRateDTO() {}

    public ZoneRateDTO(String carrier, String service, double baseRate, double perKgRate, int estimatedDays) {
        this.carrier = carrier;
        this.service = service;
        this.baseRate = baseRate;
        this.perKgRate = perKgRate;
        this.estimatedDays = estimatedDays;
    }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public double getBaseRate() { return baseRate; }
    public void setBaseRate(double baseRate) { this.baseRate = baseRate; }

    public double getPerKgRate() { return perKgRate; }
    public void setPerKgRate(double perKgRate) { this.perKgRate = perKgRate; }

    public int getEstimatedDays() { return estimatedDays; }
    public void setEstimatedDays(int estimatedDays) { this.estimatedDays = estimatedDays; }
}
