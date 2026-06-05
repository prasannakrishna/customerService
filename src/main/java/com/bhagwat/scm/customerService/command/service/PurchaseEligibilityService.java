package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.customerService.command.entity.CustomerOrder;
import com.bhagwat.scm.customerService.command.repository.CustomerOrderRepository;
import com.bhagwat.scm.customerService.rest.CartServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Purchase eligibility rules:
 *
 * 1. RETAIL (one-time): Customer can buy 1 unit of a product+variant from a seller ONCE.
 *    - If product has multiple variants, customer can order each variant once.
 *    - Price = retail price.
 *    - Quantity fixed at 1.
 *
 * 2. SUBSCRIPTION (community): Customer subscribes via community cart.
 *    - Price = community price (discounted).
 *    - Recurring delivery.
 *    - No duplicate restriction (managed by subscription lifecycle).
 */
@Service @Slf4j @RequiredArgsConstructor
public class PurchaseEligibilityService {

    private final CustomerOrderRepository orderRepo;
    private final CartServiceClient cartServiceClient;

    /**
     * Check if customer can place a retail order for this product+seller+variant.
     * Returns error message if not eligible, null if eligible.
     */
    public String checkRetailEligibility(String customerId, String productId, String sellerId, String variantId) {
        UUID custUuid = toUUID(customerId);
        UUID prodUuid = toUUID(productId);
        UUID sellerUuid = toUUID(sellerId);
        UUID varUuid = toUUID(variantId);

        if (custUuid == null || prodUuid == null || sellerUuid == null) {
            return "Invalid customer, product, or seller ID";
        }

        // Check: has customer already ordered this exact product+seller+variant?
        if (varUuid != null) {
            if (orderRepo.existsByCustomerIdAndProductIdAndSellerIdAndVariantId(custUuid, prodUuid, sellerUuid, varUuid)) {
                return "You have already purchased this variant from this seller. Try a different variant.";
            }
        } else {
            // No variant specified — check if they ordered this product+seller at all
            if (orderRepo.existsByCustomerIdAndProductIdAndSellerId(custUuid, prodUuid, sellerUuid)) {
                return "You have already purchased this product from this seller.";
            }
        }

        return null; // Eligible
    }

    /**
     * Get purchase history for a customer+seller — which product+variants already ordered.
     * Used by UI to grey out already-purchased items.
     */
    public Map<String, Object> getPurchaseHistory(String customerId, String sellerId) {
        UUID custUuid = toUUID(customerId);
        UUID sellerUuid = toUUID(sellerId);
        if (custUuid == null || sellerUuid == null) return Map.of("orders", List.of());

        List<CustomerOrder> orders = orderRepo.findByCustomerIdAndSellerId(custUuid, sellerUuid);

        // Group by productId → list of variantIds already purchased
        Map<String, List<String>> purchased = orders.stream()
                .filter(o -> "RETAIL".equals(o.getOrderType()))
                .collect(Collectors.groupingBy(
                        o -> o.getProductId().toString(),
                        Collectors.mapping(
                                o -> o.getVariantId() != null ? o.getVariantId().toString() : "default",
                                Collectors.toList())));

        // Also check active subscriptions
        Set<String> subscribedProducts = new HashSet<>();
        try {
            List<Map<String, Object>> subs = cartServiceClient.getSubscriptions(customerId);
            subs.stream()
                    .filter(s -> "ACTIVE".equals(s.get("subscriptionStatus")) || "HOLD".equals(s.get("subscriptionStatus")))
                    .forEach(s -> subscribedProducts.add(String.valueOf(s.get("productId"))));
        } catch (Exception e) {
            log.debug("Could not fetch subscriptions: {}", e.getMessage());
        }

        return Map.of(
                "retailPurchased", purchased,
                "subscribedProducts", subscribedProducts);
    }

    private UUID toUUID(String val) {
        if (val == null || val.isBlank()) return null;
        try { return UUID.fromString(val); }
        catch (IllegalArgumentException e) { return null; }
    }
}
