package com.bhagwat.scm.customerService.rest;

import com.bhagwat.scm.core.rest.api.ApiClient;
import com.bhagwat.scm.core.rest.config.ServiceApiRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component @Slf4j @RequiredArgsConstructor
public class CommunityManagerClient {

    private final ApiClient apiClient;
    private final ServiceApiRegistry registry;

    @SuppressWarnings("unchecked")
    public Set<String> getKeywordsForUser(String customerId) {
        Set<String> allKeywords = new LinkedHashSet<>();
        try {
            String encoded = URLEncoder.encode(customerId, StandardCharsets.UTF_8);
            List<Map<String, Object>> members = fetchList("community-member-of", Map.of("customerId", encoded));
            List<Map<String, Object>> moderators = fetchList("community-moderator-of", Map.of("customerId", encoded));
            extractKeywords(members, allKeywords);
            extractKeywords(moderators, allKeywords);
        } catch (Exception e) {
            log.error("Failed to fetch communities for user {}: {}", customerId, e.getMessage());
        }
        return allKeywords;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchList(String configName, Map<String, String> queryParams) {
        try {
            ResponseEntity<List> resp = apiClient.invoke(
                    registry.getConfig(configName), null, queryParams, null, List.class);
            return resp.getBody() != null ? resp.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.warn("API call [{}] failed: {}", configName, e.getMessage());
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private void extractKeywords(List<Map<String, Object>> communities, Set<String> keywords) {
        for (Map<String, Object> community : communities) {
            Object kw = community.get("keywords");
            if (kw instanceof Collection) {
                ((Collection<String>) kw).forEach(k -> { if (k != null && !k.isBlank()) keywords.add(k); });
            }
        }
    }
}
