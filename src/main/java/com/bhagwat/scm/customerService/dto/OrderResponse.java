package com.bhagwat.scm.customerService.dto;

import com.bhagwat.scm.customerService.constant.CustomerOrderStatus;
import com.bhagwat.scm.customerService.constant.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;
    private UUID orderId;
    private UUID customerId;
    private UUID productId;
    private UUID variantId;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal amount;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private String currency;
    private CustomerOrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private String trackingId;
    private Instant orderCreatedDate;
    private Instant shipByDate;
    private Instant deliveryByDate;
    private Instant deliveryDate;
    private UUID communityId;
    private UUID sellerId;
    private String cancellationReason;
}
