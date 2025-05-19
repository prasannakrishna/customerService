package com.bhagwat.scm.customerService.command.events;

import com.bhagwat.scm.customerService.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreatedEvent {
    String id;
    String fname;
    String mname;
    String lname;
    String email;
    String mobileNumber;
    boolean isEmailVerified;
    boolean isMobileVerified;
    List<AddressDto> addresses;
}
