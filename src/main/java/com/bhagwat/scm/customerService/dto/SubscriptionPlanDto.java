package com.bhagwat.scm.customerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SubscriptionPlanDto {
    private String id;
    private String name;
    private BigDecimal price;
    private String billingCycle;
    private String description;
}
