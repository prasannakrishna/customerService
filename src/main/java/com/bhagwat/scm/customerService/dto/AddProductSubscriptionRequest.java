package com.bhagwat.scm.customerService.dto;

import com.bhagwat.scm.inventorydto.SubscriptionCalendarUnit;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AddProductSubscriptionRequest {
    private UUID customerId;
    private UUID productId;
    private UUID variantId;
    private Integer quantity;
    private SubscriptionCalendarUnit frequency;
    private Integer duration;
    private String communityId;
    private String sellerId;
    private BigDecimal amount;
    private String currency;
    private boolean notifyOnCheckouts;
    private boolean remind;
}
