package com.bhagwat.scm.customerService.dto;

import com.bhagwat.scm.customerService.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerCreatedEvent {
    private String id;
    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileNumber;
    private Address address;
}

