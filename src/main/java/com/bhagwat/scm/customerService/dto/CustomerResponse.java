package com.bhagwat.scm.customerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    private Long customerId;
    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileNumber;
    private boolean isEmailVerified;
    private boolean isMobileVerified;
    private List<AddressDto> addresses;
}
