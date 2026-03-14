package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.service.SubscriptionService;
import com.bhagwat.scm.customerService.dto.AddProductSubscriptionRequest;
import com.bhagwat.scm.customerService.dto.ProductSubscriptionResponse;
import com.bhagwat.scm.customerService.dto.SubscriptionPlanDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
public class ProductSubscriptionController {

    private final SubscriptionService subscriptionService;

    public ProductSubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanDto>> getPlans() {
        return ResponseEntity.ok(subscriptionService.getPlans());
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductSubscriptionResponse>> getProductSubscriptions(@RequestParam UUID customerId) {
        return ResponseEntity.ok(subscriptionService.getCustomerSubscriptions(customerId));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductSubscriptionResponse> addProductSubscription(
            @RequestBody AddProductSubscriptionRequest request) {
        return new ResponseEntity<>(subscriptionService.addProductSubscription(request), HttpStatus.CREATED);
    }

    /** Called after payment is confirmed — locks the subscription (no cancel allowed after this). */
    @PutMapping("/{subscriptionId}/confirm-payment")
    public ResponseEntity<ProductSubscriptionResponse> confirmPayment(@PathVariable UUID subscriptionId) {
        return ResponseEntity.ok(subscriptionService.confirmPayment(subscriptionId));
    }

    /**
     * Hold subscription for the current cycle:
     *   DAILY  → holds until end of today
     *   WEEKLY / BIWEEKLY → holds until end of current week
     *   MONTHLY+ → holds until end of current month
     */
    @PutMapping("/{subscriptionId}/hold")
    public ResponseEntity<ProductSubscriptionResponse> holdSubscription(@PathVariable UUID subscriptionId) {
        return ResponseEntity.ok(subscriptionService.holdSubscription(subscriptionId));
    }

    /** Resume a held subscription back to ACTIVE. */
    @PutMapping("/{subscriptionId}/resume")
    public ResponseEntity<ProductSubscriptionResponse> resumeSubscription(@PathVariable UUID subscriptionId) {
        return ResponseEntity.ok(subscriptionService.resumeSubscription(subscriptionId));
    }

    /**
     * Cancel — only allowed before payment is confirmed.
     * Returns 409 Conflict if already paid (use /hold instead).
     */
    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Object> cancelSubscription(@PathVariable UUID subscriptionId) {
        try {
            subscriptionService.cancelSubscription(subscriptionId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage(), "hint", "Use PUT /hold to pause this subscription"));
        }
    }
}
