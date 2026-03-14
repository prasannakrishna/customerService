package com.bhagwat.scm.customerService.command.repository;

import com.bhagwat.scm.customerService.command.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    List<PaymentMethod> findByCustomerId(UUID customerId);
}
