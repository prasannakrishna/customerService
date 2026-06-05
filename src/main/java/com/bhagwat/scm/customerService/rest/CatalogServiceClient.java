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
public class CatalogServiceClient {

    private final ApiClient apiClient;
    private final ServiceApiRegistry registry;

    public Map<String, Object> getAllProducts(int page, int size) {
        return get("catalog-products-all", Map.of("page", String.valueOf(page), "size", String.valueOf(size)));
    }

    public Map<String, Object> searchProducts(List<String> searchKeys, int page, int size) {
        Map<String, String> params = Map.of(
                "searchKeys", String.join(",", searchKeys),
                "page", String.valueOf(page), "size", String.valueOf(size));
        return get("catalog-products-search", params);
    }

    public Map<String, Object> getCatalogForUser(String customerId) {
        return get("catalog-products-for-user", Map.of("customerId", customerId));
    }

    public Map<String, Object> getProductsGroupedByCommunity(int page, int size) {
        return get("catalog-products-grouped", Map.of("page", String.valueOf(page), "size", String.valueOf(size)));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> get(String configName, Map<String, String> queryParams) {
        try {
            ResponseEntity<Map> resp = apiClient.invoke(
                    registry.getConfig(configName), null, queryParams, null, Map.class);
            return resp.getBody() != null ? resp.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("API call [{}] failed: {}", configName, e.getMessage());
            return Collections.emptyMap();
        }
    }
}
