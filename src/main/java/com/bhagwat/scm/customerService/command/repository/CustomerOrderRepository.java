package com.bhagwat.scm.customerService.command.repository;

import com.bhagwat.scm.customerService.command.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {
    List<CustomerOrder> findByCustomerIdOrderByOrderCreatedDateDesc(UUID customerId);
    Optional<CustomerOrder> findByOrderId(UUID orderId);

    /** Check if customer already ordered this product+seller+variant (for one-time retail purchase guard) */
    boolean existsByCustomerIdAndProductIdAndSellerIdAndVariantId(UUID customerId, UUID productId, UUID sellerId, UUID variantId);

    /** Check if customer already ordered this product+seller (any variant) */
    boolean existsByCustomerIdAndProductIdAndSellerId(UUID customerId, UUID productId, UUID sellerId);

    /** Get all product+variant combos a customer has ordered from a seller */
    List<CustomerOrder> findByCustomerIdAndSellerId(UUID customerId, UUID sellerId);
}
