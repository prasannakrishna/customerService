package com.bhagwat.scm.userService.controller;

import com.bhagwat.scm.userService.dto.SubscriptionRequest;
import com.bhagwat.scm.userService.dto.SubscriptionResponse;
import com.bhagwat.scm.userService.service.ApplicationSubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
public class ApplicationSubscriptionController {

    private final ApplicationSubscriptionService subscriptionService;

    public ApplicationSubscriptionController(ApplicationSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/manage")
    public ResponseEntity<SubscriptionResponse> manageSubscription(@Valid @RequestBody SubscriptionRequest request) {
        SubscriptionResponse response = subscriptionService.addOrUpdateSubscription(request);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/deactivate/{appSubId}")
    public ResponseEntity<SubscriptionResponse> deactivateSubscription(@PathVariable String appSubId) {
        SubscriptionResponse response = subscriptionService.deactivateSubscription(appSubId);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}