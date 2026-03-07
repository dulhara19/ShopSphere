package com.shopsphere.shipping.dto;

public class FlatRateDTO {
    private String service;
    private String estimatedDays;
    private double rate;
    private String currency;

    public FlatRateDTO() {}

    public FlatRateDTO(String service, String estimatedDays, double rate, String currency) {
        this.service = service;
        this.estimatedDays = estimatedDays;
        this.rate = rate;
        this.currency = currency;
    }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getEstimatedDays() { return estimatedDays; }
    public void setEstimatedDays(String estimatedDays) { this.estimatedDays = estimatedDays; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
