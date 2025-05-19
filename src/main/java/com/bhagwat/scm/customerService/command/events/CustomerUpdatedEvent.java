package com.bhagwat.scm.customerService.command.events;

import com.bhagwat.scm.customerService.command.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerUpdatedEvent {
    private String id;
    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileNumber;
    private Address address;
}

