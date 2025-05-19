package com.bhagwat.scm.customerService.command.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShipmentCreatedEvent {
    private String shipmentId;
    private String orderId;
    private String sellerId;
    private String customerId;

    private Double price;
    private Double weight;

    private Double volume;
}


