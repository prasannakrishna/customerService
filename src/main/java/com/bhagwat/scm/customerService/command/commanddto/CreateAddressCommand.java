package com.bhagwat.scm.customerService.command.commanddto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAddressCommand {
    @TargetAggregateIdentifier
    String addressId; // Usually used if this command targets an Address aggregate. You may use UUID or null initially.

    String addressLine1;
    String addressLine2;
    String city;
    String post;
    String pincode;
    String state;
    String country;
    String landMark;
    double longitude;
    double latitude;
    boolean isPrimaryAddress;
    String customerId;
}
