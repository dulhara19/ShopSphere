package com.shopsphere.shipping.dto;

import java.util.List;

public class AddressValidationResponse {
    private boolean valid;
    private AddressDTO standardizedAddress;
    private List<String> suggestions;
    private List<String> errors;

    public AddressValidationResponse() {}

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public AddressDTO getStandardizedAddress() { return standardizedAddress; }
    public void setStandardizedAddress(AddressDTO standardizedAddress) { this.standardizedAddress = standardizedAddress; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
