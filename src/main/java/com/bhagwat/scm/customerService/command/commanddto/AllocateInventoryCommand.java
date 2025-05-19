package com.bhagwat.scm.customerService.command.commanddto;

import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
public class AllocateInventoryCommand {
    @TargetAggregateIdentifier
    private final String inventoryId;
    private final String orderId;
    private final Integer orderQuantity;

    public AllocateInventoryCommand(String inventoryId, String orderId, Integer orderQuantity) {
        this.inventoryId = inventoryId;
        this.orderId = orderId;
        this.orderQuantity = orderQuantity;
    }

    // Getters
}
