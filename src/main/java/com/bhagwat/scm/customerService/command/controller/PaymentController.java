package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.service.PaymentService;
import com.bhagwat.scm.customerService.dto.PaymentMethodDto;
import com.bhagwat.scm.customerService.dto.ProcessPaymentRequest;
import com.bhagwat.scm.customerService.dto.ProcessPaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<ProcessPaymentResponse> processPayment(@RequestBody ProcessPaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/methods")
    public ResponseEntity<List<PaymentMethodDto>> getSavedMethods(@RequestParam UUID customerId) {
        return ResponseEntity.ok(paymentService.getSavedMethods(customerId));
    }

    @PostMapping("/methods")
    public ResponseEntity<PaymentMethodDto> addMethod(@RequestBody PaymentMethodDto request) {
        return new ResponseEntity<>(paymentService.addMethod(request), HttpStatus.CREATED);
    }
}
