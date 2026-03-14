package com.bhagwat.scm.customerService.command.events;

import com.bhagwat.scm.customerService.dto.CustomerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEvent {
    private UUID eventId = UUID.randomUUID();
    private UUID customerId;
    private String eventType; // e.g., "CUSTOMER_CREATED", "CUSTOMER_UPDATED", "CUSTOMER_DELETED"
    private CustomerDto customerPayload;
}