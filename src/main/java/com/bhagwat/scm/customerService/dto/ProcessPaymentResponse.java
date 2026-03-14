package com.bhagwat.scm.customerService.dto;

import com.bhagwat.scm.customerService.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProcessPaymentResponse {
    private UUID paymentId;
    private UUID orderId;
    private PaymentStatus status;
    private String message;
}
