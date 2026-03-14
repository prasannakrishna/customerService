package com.bhagwat.scm.customerService.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CartItemRequest {
    private UUID customerId;
    private UUID productId;
    private UUID variantId;
    private Integer quantity;
    private BigDecimal price;
    private String productName;
}
