package com.bhagwat.scm.customerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {
    private String username;
    private String password;
    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileNumber;
    private List<AddressDto> addresses;
}
