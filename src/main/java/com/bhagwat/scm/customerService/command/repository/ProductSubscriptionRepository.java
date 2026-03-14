package com.bhagwat.scm.customerService.command.repository;

import com.bhagwat.scm.customerService.command.entity.ProductSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductSubscriptionRepository extends JpaRepository<ProductSubscription, UUID> {
    List<ProductSubscription> findByCustomerId(UUID customerId);
}
