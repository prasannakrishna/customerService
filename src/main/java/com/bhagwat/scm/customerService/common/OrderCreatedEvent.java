package com.bhagwat.scm.customerService.common;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderCreatedEvent {
    private final String orderId;
    private final String customerId;
    private final LocalDateTime orderCreatedDate;
    private final String consignmentId;

    public OrderCreatedEvent(String orderId, String customerId, LocalDateTime orderCreatedDate, String consignmentId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderCreatedDate = orderCreatedDate;
        this.consignmentId = consignmentId;
    }

    // Getters
}