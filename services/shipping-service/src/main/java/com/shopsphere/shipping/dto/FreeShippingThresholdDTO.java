package com.shopsphere.shipping.dto;

public class FreeShippingThresholdDTO {
    private boolean enabled;
    private double threshold;
    private String currency;

    public FreeShippingThresholdDTO() {}

    public FreeShippingThresholdDTO(boolean enabled, double threshold, String currency) {
        this.enabled = enabled;
        this.threshold = threshold;
        this.currency = currency;
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
