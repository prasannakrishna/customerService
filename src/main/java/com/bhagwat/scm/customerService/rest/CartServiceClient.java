package com.bhagwat.scm.customerService.rest;

import com.bhagwat.scm.core.rest.api.ApiClient;
import com.bhagwat.scm.core.rest.config.ServiceApiRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component @Slf4j @RequiredArgsConstructor
public class CartServiceClient {

    private final ApiClient apiClient;
    private final ServiceApiRegistry registry;

    @SuppressWarnings("unchecked")
    public Map<String, Object> addToCart(Map<String, Object> request) {
        return post("cart-add", request);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getCart(String customerId) {
        try {
            ResponseEntity<Map> resp = apiClient.invoke(registry.getConfig("cart-get", customerId), Map.class);
            return resp.getBody() != null ? resp.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.warn("cart-get failed for {}: {}", customerId, e.getMessage());
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> checkout(String customerId) {
        return post("cart-checkout", customerId, null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> addSubscription(Map<String, Object> request) {
        return post("subscription-cart-add", request);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getSubscriptions(String customerId) {
        try {
            ResponseEntity<List> resp = apiClient.invoke(registry.getConfig("subscription-cart-get", customerId), List.class);
            return resp.getBody() != null ? resp.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.warn("subscription-cart-get failed for {}: {}", customerId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> checkoutSubscriptions(String customerId) {
        return post("subscription-cart-checkout", customerId, null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> holdSubscription(String subscriptionId, String reason) {
        return post("subscription-hold", subscriptionId, Map.of("reason", reason != null ? reason : ""));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> resumeSubscription(String subscriptionId) {
        return post("subscription-resume", subscriptionId, null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> cancelSubscription(String subscriptionId) {
        return post("subscription-cancel", subscriptionId, null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String configName, Object body) {
        try {
            ResponseEntity<Map> resp = apiClient.invoke(registry.getConfig(configName), body, Map.class);
            return resp.getBody() != null ? resp.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("[{}] failed: {}", configName, e.getMessage());
            throw new RuntimeException(configName + " failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String configName, String pathVar, Object body) {
        try {
            ResponseEntity<Map> resp = apiClient.invoke(registry.getConfig(configName, pathVar), body, Map.class);
            return resp.getBody() != null ? resp.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("[{}] failed: {}", configName, e.getMessage());
            throw new RuntimeException(configName + " failed: " + e.getMessage());
        }
    }
}
