package com.bhagwat.scm.customerService.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentMethodDto {
    private UUID id;
    private UUID customerId;
    private String type;       // CREDIT_CARD, DEBIT_CARD, PAYPAL, UPI
    private String label;      // e.g. "Visa ending in 4242"
    private String lastFour;
    private String expiryMonth;
    private String expiryYear;
    private boolean isDefault;
}
