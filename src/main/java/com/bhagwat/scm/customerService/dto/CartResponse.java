package com.bhagwat.scm.customerService.dto;

import com.bhagwat.scm.customerService.constant.ShoppingBagStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CartResponse {
    private UUID cartId;
    private UUID customerId;
    private ShoppingBagStatus status;
    private Integer totalItemCount;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;
}
