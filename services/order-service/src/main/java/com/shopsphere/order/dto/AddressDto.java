package com.shopsphere.order.dto;

import com.shopsphere.order.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private UUID id;
    private String fullName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;

    public static AddressDto from(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDto.builder()
            .id(address.getId())
            .fullName(address.getFullName())
            .addressLine1(address.getAddressLine1())
            .addressLine2(address.getAddressLine2())
            .city(address.getCity())
            .state(address.getState())
            .postalCode(address.getPostalCode())
            .country(address.getCountry())
            .phone(address.getPhone())
            .build();
    }

    public Address toEntity() {
        return Address.builder()
            .id(this.id)
            .fullName(this.fullName)
            .addressLine1(this.addressLine1)
            .addressLine2(this.addressLine2)
            .city(this.city)
            .state(this.state)
            .postalCode(this.postalCode)
            .country(this.country)
            .phone(this.phone)
            .build();
    }
}
