package com.bhagwat.scm.customerService.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PlaceOrderRequest {
    private UUID customerId;
    private UUID productId;
    private UUID variantId;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private String currency;
    private UUID communityId;
    private UUID sellerId;
    private String inventoryKey;
    private UUID shoppingBagId;
    private AddressDto shippingAddress;
}
