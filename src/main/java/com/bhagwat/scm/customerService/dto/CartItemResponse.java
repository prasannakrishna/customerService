package com.bhagwat.scm.customerService.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CartItemResponse {
    private UUID id;
    private UUID productId;
    private UUID variantId;
    private Integer quantity;
    private BigDecimal price;
    private String productName;
}
