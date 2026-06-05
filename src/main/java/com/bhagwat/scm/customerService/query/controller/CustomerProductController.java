package com.bhagwat.scm.customerService.query.controller;

import com.bhagwat.scm.customerService.rest.CatalogServiceClient;
import com.bhagwat.scm.customerService.rest.CommunityManagerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final CatalogServiceClient catalogServiceClient;
    private final CommunityManagerClient communityManagerClient;

    /**
     * List all products with variants from catalog service.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("getProducts called with page={}, size={}", page, size);
        return ResponseEntity.ok(catalogServiceClient.getAllProducts(page, size));
    }

    /**
     * Search products by search keys — returns products sorted by relevance.
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam List<String> searchKeys,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("searchProducts called with searchKeys={}, page={}, size={}", searchKeys, page, size);
        return ResponseEntity.ok(catalogServiceClient.searchProducts(searchKeys, page, size));
    }

    /**
     * Personalized product catalog for a user, grouped by community.
     *
     * Flow:
     *   1. Get user's communities (from communityManager)
     *   2. Get community products ranked by weight (40% keyword + 30% subscription + 30% rating)
     *   3. Check stock availability (from catalogDb)
     *   4. Filter out already-subscribed products (from cartService)
     *   5. Group by community, ordered by ranking weight
     */
    @GetMapping("/for-me")
    public ResponseEntity<Map<String, Object>> getProductsForMe(
            @RequestParam String customerId) {
        log.info("getProductsForMe called for customerId={}", customerId);
        return ResponseEntity.ok(catalogServiceClient.getCatalogForUser(customerId));
    }
}
