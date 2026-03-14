package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.customerService.command.entity.PaymentMethod;
import com.bhagwat.scm.customerService.command.repository.PaymentMethodRepository;
import com.bhagwat.scm.customerService.constant.PaymentStatus;
import com.bhagwat.scm.customerService.dto.PaymentMethodDto;
import com.bhagwat.scm.customerService.dto.ProcessPaymentRequest;
import com.bhagwat.scm.customerService.dto.ProcessPaymentResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentService(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public ProcessPaymentResponse processPayment(ProcessPaymentRequest request) {
        // Simulate payment processing — integrate with payment gateway in future
        return new ProcessPaymentResponse(
                UUID.randomUUID(),
                request.getOrderId(),
                PaymentStatus.PAID,
                "Payment processed successfully"
        );
    }

    public List<PaymentMethodDto> getSavedMethods(UUID customerId) {
        return paymentMethodRepository.findByCustomerId(customerId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public PaymentMethodDto addMethod(PaymentMethodDto request) {
        PaymentMethod method = new PaymentMethod();
        method.setCustomerId(request.getCustomerId());
        method.setType(request.getType());
        method.setLabel(request.getLabel());
        method.setLastFour(request.getLastFour());
        method.setExpiryMonth(request.getExpiryMonth());
        method.setExpiryYear(request.getExpiryYear());
        method.setDefault(request.isDefault());

        if (request.isDefault()) {
            paymentMethodRepository.findByCustomerId(request.getCustomerId())
                    .forEach(m -> { m.setDefault(false); paymentMethodRepository.save(m); });
        }

        return toDto(paymentMethodRepository.save(method));
    }

    private PaymentMethodDto toDto(PaymentMethod m) {
        PaymentMethodDto dto = new PaymentMethodDto();
        dto.setId(m.getId());
        dto.setCustomerId(m.getCustomerId());
        dto.setType(m.getType());
        dto.setLabel(m.getLabel());
        dto.setLastFour(m.getLastFour());
        dto.setExpiryMonth(m.getExpiryMonth());
        dto.setExpiryYear(m.getExpiryYear());
        dto.setDefault(m.isDefault());
        return dto;
    }
}
