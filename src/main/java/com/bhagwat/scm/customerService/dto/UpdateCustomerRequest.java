package com.bhagwat.scm.customerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {
    private UUID id;
    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileNumber;
    private List<AddressDto> addresses;
}