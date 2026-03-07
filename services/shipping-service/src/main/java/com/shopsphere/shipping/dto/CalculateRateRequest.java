package com.shopsphere.shipping.dto;

import java.util.List;

public class CalculateRateRequest {
    private AddressDTO fromAddress;
    private AddressDTO toAddress;
    private List<RateItem> items;

    public AddressDTO getFromAddress() { return fromAddress; }
    public void setFromAddress(AddressDTO fromAddress) { this.fromAddress = fromAddress; }

    public AddressDTO getToAddress() { return toAddress; }
    public void setToAddress(AddressDTO toAddress) { this.toAddress = toAddress; }

    public List<RateItem> getItems() { return items; }
    public void setItems(List<RateItem> items) { this.items = items; }

    public static class RateItem {
        private double weight;

        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }
    }
}
