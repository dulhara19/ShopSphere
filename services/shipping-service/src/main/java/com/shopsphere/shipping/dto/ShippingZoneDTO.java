package com.shopsphere.shipping.dto;

import java.util.List;

public class ShippingZoneDTO {
    private String id;
    private String name;
    private List<String> countries;
    private List<ZoneRateDTO> rates;

    public ShippingZoneDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getCountries() { return countries; }
    public void setCountries(List<String> countries) { this.countries = countries; }

    public List<ZoneRateDTO> getRates() { return rates; }
    public void setRates(List<ZoneRateDTO> rates) { this.rates = rates; }
}
