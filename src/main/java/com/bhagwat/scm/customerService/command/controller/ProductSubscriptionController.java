package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.service.SubscriptionService;
import com.bhagwat.scm.customerService.dto.AddProductSubscriptionRequest;
import com.bhagwat.scm.customerService.dto.ProductSubscriptionResponse;
import com.bhagwat.scm.customerService.dto.SubscriptionPlanDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> cancelSubscription(@PathVariable UUID subscriptionId) {
        subscriptionService.cancelSubscription(subscriptionId);
        return ResponseEntity.noContent().build();
    }
}
