package com.shopsphere.shipping.dto;

public class CarrierRateDTO {
    private String carrier;
    private String service;
    private double rate;
    private String currency;
    private int estimatedDays;
    private String deliveryDate;

    public CarrierRateDTO() {}

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public int getEstimatedDays() { return estimatedDays; }
    public void setEstimatedDays(int estimatedDays) { this.estimatedDays = estimatedDays; }

    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
}
