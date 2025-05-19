package com.bhagwat.scm.customerService.command.events;

public class InventoryAllocatedEvent {
    private final String inventoryId;
    private final String orderId;
    private final Integer orderQuantity;

    public InventoryAllocatedEvent(String inventoryId, String orderId, Integer orderQuantity) {
        this.inventoryId = inventoryId;
        this.orderId = orderId;
        this.orderQuantity = orderQuantity;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

}
