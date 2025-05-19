package com.bhagwat.scm.customerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private Long addressId;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String post;
    private String pincode;
    private String state;
    private String country;
    private String landMark;
    private double longitude;
    private double latitude;
    private boolean isPrimaryAddress;
    private String customerId;
}

