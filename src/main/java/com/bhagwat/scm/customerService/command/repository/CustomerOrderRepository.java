package com.bhagwat.scm.customerService.command.repository;

import com.bhagwat.scm.customerService.command.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {
    List<CustomerOrder> findByCustomerIdOrderByOrderCreatedDateDesc(UUID customerId);
    Optional<CustomerOrder> findByOrderId(UUID orderId);
}
