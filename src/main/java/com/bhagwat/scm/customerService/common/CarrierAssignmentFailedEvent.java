package com.bhagwat.scm.customerService.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarrierAssignmentFailedEvent {
    private String shipmentId;
    private String reason;

    // Constructor, Getters, and Setters
}

