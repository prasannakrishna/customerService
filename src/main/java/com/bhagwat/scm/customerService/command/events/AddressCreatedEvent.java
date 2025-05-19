package com.bhagwat.scm.customerService.command.events;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddressCreatedEvent {
    String addressId;

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
