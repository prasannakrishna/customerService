package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.core.rest.api.ApiClient;
import com.bhagwat.scm.core.rest.config.ServiceApiRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Async inter-service client using ApiClient + ServiceApiRegistry.
 * Fires parallel calls on a dedicated thread pool.
 */
@Service @Slf4j @RequiredArgsConstructor
public class AsyncServiceClient {

    private final ApiClient apiClient;
    private final ServiceApiRegistry registry;

    private final Executor httpExecutor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 4);

    public CompletableFuture<Map> asyncGet(String configName, String... pathVars) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<Map> resp = apiClient.invoke(registry.getConfig(configName, pathVars), Map.class);
                return resp.getBody();
            } catch (Exception e) {
                log.warn("Async [{}] failed: {}", configName, e.getMessage());
                return Map.of("error", e.getMessage());
            }
        }, httpExecutor);
    }

    public CompletableFuture<Map> asyncPost(String configName, Object body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<Map> resp = apiClient.invoke(registry.getConfig(configName), body, Map.class);
                return resp.getBody();
            } catch (Exception e) {
                log.warn("Async POST [{}] failed: {}", configName, e.getMessage());
                return Map.of("error", e.getMessage());
            }
        }, httpExecutor);
    }

    public CompletableFuture<Map> getCart(String customerId) {
        return asyncGet("cart-get", customerId);
    }

    public CompletableFuture<Map> getCommunities(String customerId) {
        return asyncGet("community-member-of", customerId);
    }

    public CompletableFuture<Map<String, Object>> getHomePageData(String customerId) {
        CompletableFuture<Map> cartFuture = getCart(customerId);
        CompletableFuture<Map> communityFuture = getCommunities(customerId);
        return CompletableFuture.allOf(cartFuture, communityFuture)
                .thenApply(v -> Map.of("cart", cartFuture.join(), "communities", communityFuture.join()));
    }
}
