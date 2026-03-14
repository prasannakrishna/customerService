package com.bhagwat.scm.customerService.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProcessPaymentRequest {
    private UUID customerId;
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;  // CREDIT_CARD, PAYPAL, etc.
    private UUID paymentMethodId;
}
