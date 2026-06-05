package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.entity.CustomerOrder;
import com.bhagwat.scm.customerService.command.repository.CustomerOrderRepository;
import com.bhagwat.scm.customerService.constant.CustomerOrderStatus;
import com.bhagwat.scm.customerService.constant.PaymentStatus;
import com.bhagwat.scm.customerService.rest.CartServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartServiceClient cartServiceClient;
    private final CustomerOrderRepository orderRepository;
    private final com.bhagwat.scm.customerService.command.service.PurchaseEligibilityService eligibilityService;

    /**
     * Add item to cart — enforces one-time retail purchase rule.
     * Quantity is always 1 for retail. Customer cannot re-order same product+seller+variant.
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> request) {
        String customerId = (String) request.get("customerId");
        String productId = (String) request.get("productId");
        String sellerId = (String) request.get("sellerId");
        String variantId = (String) request.get("variantId");

        // Enforce: 1 unit only for retail
        request.put("quantity", 1);
        request.put("orderType", "RETAIL");

        String error = eligibilityService.checkRetailEligibility(customerId, productId, sellerId, variantId);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }

        return ResponseEntity.ok(cartServiceClient.addToCart(request));
    }

    /** Get purchase eligibility — which products/variants customer can still buy */
    @GetMapping("/{customerId}/eligibility")
    public ResponseEntity<Map<String, Object>> getEligibility(
            @PathVariable String customerId, @RequestParam String sellerId) {
        return ResponseEntity.ok(eligibilityService.getPurchaseHistory(customerId, sellerId));
    }

    /**
     * Get active cart — proxies to CartService.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> getCart(@PathVariable String customerId) {
        return ResponseEntity.ok(cartServiceClient.getCart(customerId));
    }

    /**
     * Checkout cart — calls CartService checkout, then creates CustomerOrders
     * from the checked-out items so customer can see them in "My Orders".
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/checkout/{customerId}")
    public ResponseEntity<Map<String, Object>> checkout(@PathVariable String customerId) {
        Map<String, Object> cartResponse = cartServiceClient.checkout(customerId);

        // Create CustomerOrders from cart items
        List<Map<String, Object>> items = (List<Map<String, Object>>) cartResponse.getOrDefault("items", Collections.emptyList());
        List<UUID> orderIds = new ArrayList<>();

        for (Map<String, Object> item : items) {
            try {
                CustomerOrder order = new CustomerOrder();
                order.setOrderId(UUID.randomUUID());
                order.setCustomerId(toUUID(customerId));
                order.setProductId(toUUID(getString(item, "productId")));
                order.setVariantId(toUUID(getString(item, "variantId")));
                order.setQuantity(getInt(item, "quantity", 1));
                order.setPricePerUnit(getBigDecimal(item, "pricePerUnit", BigDecimal.ZERO));
                order.setAmount(getBigDecimal(item, "totalItemPrice", BigDecimal.ZERO));
                order.setCurrency("INR");
                order.setCommunityId(toUUID(getString(item, "communityId")));
                order.setSellerId(toUUID(getString(item, "sellerId")));
                order.setInventoryKey(getString(item, "inventoryKey") != null ? getString(item, "inventoryKey") : "default");
                order.setOrderCreatedDate(Instant.now());
                order.setShipByDate(Instant.now().plus(3, ChronoUnit.DAYS));
                order.setDeliveryByDate(Instant.now().plus(7, ChronoUnit.DAYS));
                order.setOrderStatus(CustomerOrderStatus.PENDING);
                order.setPaymentStatus(PaymentStatus.PENDING);
                order.setShippingCost(BigDecimal.valueOf(5.99));
                order.setTaxAmount(order.getAmount().multiply(BigDecimal.valueOf(0.08)));
                order.setOrderType(getString(item, "orderType") != null ? getString(item, "orderType") : "RETAIL");

                CustomerOrder saved = orderRepository.save(order);
                orderIds.add(saved.getOrderId());
                log.info("Created CustomerOrder {} for product {}", saved.getOrderId(), getString(item, "productId"));
            } catch (Exception e) {
                log.warn("Failed to create order for item {}: {}", item, e.getMessage());
            }
        }

        Map<String, Object> response = new HashMap<>(cartResponse);
        response.put("orderIds", orderIds);
        response.put("message", "Checkout successful. " + orderIds.size() + " order(s) created.");
        return ResponseEntity.ok(response);
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    private int getInt(Map<String, Object> map, String key, int defaultVal) {
        Object v = map.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        return defaultVal;
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key, BigDecimal defaultVal) {
        Object v = map.get(key);
        if (v instanceof Number) return BigDecimal.valueOf(((Number) v).doubleValue());
        return defaultVal;
    }

    private UUID toUUID(String val) {
        if (val == null || val.isBlank()) return null;
        try { return UUID.fromString(val); }
        catch (IllegalArgumentException e) { return null; }
    }

    // ── Subscription cart endpoints ─────────────────────────────────────

    /** Add subscription — checks capacity from catalogService before accepting. Uses community price. */
    @PostMapping("/subscription/add")
    public ResponseEntity<Map<String, Object>> addSubscription(@RequestBody Map<String, Object> request) {
        // Validate subscription capacity before accepting
        String productId = (String) request.get("productId");
        String sellerId = (String) request.get("sellerId");
        String frequency = (String) request.getOrDefault("frequency", "MONTH");

        if (productId != null && sellerId != null) {
            String error = checkSubscriptionCapacity(productId, sellerId, frequency);
            if (error != null) {
                return ResponseEntity.badRequest().body(Map.of("error", error));
            }
        }

        request.put("orderType", "SUBSCRIPTION");
        return ResponseEntity.ok(cartServiceClient.addSubscription(request));
    }

    private String checkSubscriptionCapacity(String productId, String sellerId, String frequency) {
        try {
            var resp = new org.springframework.web.client.RestTemplate()
                    .getForObject("http://localhost:8089/api/catalog/products/" + productId
                            + "/subscription-capacity?sellerId=" + sellerId, java.util.Map.class);
            if (resp == null) return null;
            Boolean canAccept = (Boolean) resp.get("canAcceptNewSubscriptions");
            if (!Boolean.TRUE.equals(canAccept)) {
                return (String) resp.getOrDefault("message", "Subscription capacity exhausted for this product.");
            }
            // Check frequency-specific capacity
            @SuppressWarnings("unchecked")
            Map<String, Object> byFreq = (Map<String, Object>) resp.get("availableByFrequency");
            if (byFreq != null) {
                Object avail = byFreq.get(frequency.toUpperCase());
                if (avail != null && ((Number) avail).intValue() <= 0) {
                    return "No capacity for " + frequency + " subscriptions. Try a less frequent plan.";
                }
            }
        } catch (Exception e) {
            log.debug("Capacity check skipped (catalogService unavailable): {}", e.getMessage());
        }
        return null; // OK to proceed
    }

    /** Get active subscriptions for a customer */
    @SuppressWarnings("unchecked")
    @GetMapping("/subscription/{customerId}")
    public ResponseEntity<List<Map<String, Object>>> getSubscriptions(@PathVariable String customerId) {
        return ResponseEntity.ok(cartServiceClient.getSubscriptions(customerId));
    }

    /** Checkout subscriptions — marks first cycle, creates orders */
    @PostMapping("/subscription/checkout/{customerId}")
    public ResponseEntity<Map<String, Object>> checkoutSubscriptions(@PathVariable String customerId) {
        return ResponseEntity.ok(cartServiceClient.checkoutSubscriptions(customerId));
    }

    /** Hold a subscription */
    @PostMapping("/subscription/{subscriptionId}/hold")
    public ResponseEntity<Map<String, Object>> holdSubscription(
            @PathVariable String subscriptionId,
            @RequestParam(required = false, defaultValue = "") String reason) {
        return ResponseEntity.ok(cartServiceClient.holdSubscription(subscriptionId, reason));
    }

    /** Resume a held subscription */
    @PostMapping("/subscription/{subscriptionId}/resume")
    public ResponseEntity<Map<String, Object>> resumeSubscription(@PathVariable String subscriptionId) {
        return ResponseEntity.ok(cartServiceClient.resumeSubscription(subscriptionId));
    }

    /** Cancel a subscription */
    @PostMapping("/subscription/{subscriptionId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelSubscription(@PathVariable String subscriptionId) {
        return ResponseEntity.ok(cartServiceClient.cancelSubscription(subscriptionId));
    }
}
