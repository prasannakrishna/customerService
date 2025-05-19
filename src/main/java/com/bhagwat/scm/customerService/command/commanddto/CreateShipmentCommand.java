package com.bhagwat.scm.customerService.command.commanddto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateShipmentCommand {
    private String shipmentId;
    private String orderId;
    private String sellerId;
    private String customerId;

    private Double price;
    private Double weight;

    private Double volume;
}

