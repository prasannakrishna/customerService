package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.customerService.command.entity.ProductSubscription;
import com.bhagwat.scm.customerService.command.repository.ProductSubscriptionRepository;
import com.bhagwat.scm.customerService.constant.SubscriptionStatus;
import com.bhagwat.scm.customerService.dto.AddProductSubscriptionRequest;
import com.bhagwat.scm.customerService.dto.ProductSubscriptionResponse;
import com.bhagwat.scm.customerService.dto.SubscriptionPlanDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final ProductSubscriptionRepository subscriptionRepository;

    public SubscriptionService(ProductSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<SubscriptionPlanDto> getPlans() {
        return List.of(
                new SubscriptionPlanDto("free", "Free", BigDecimal.ZERO, "monthly", "Basic access, no perks"),
                new SubscriptionPlanDto("silver", "Silver", new BigDecimal("9.99"), "monthly", "Priority support & early access"),
                new SubscriptionPlanDto("gold", "Gold", new BigDecimal("19.99"), "monthly", "All Silver perks + exclusive deals"),
                new SubscriptionPlanDto("platinum", "Platinum", new BigDecimal("39.99"), "monthly", "All Gold perks + free shipping on every order")
        );
    }

    @Transactional
    public ProductSubscriptionResponse addProductSubscription(AddProductSubscriptionRequest request) {
        ProductSubscription sub = new ProductSubscription();
        sub.setCustomerId(request.getCustomerId());
        sub.setProductId(request.getProductId());
        sub.setProductVariantId(request.getVariantId());
        sub.setQuantity(request.getQuantity());
        sub.setSubscriptionCalendarUnit(request.getFrequency());
        sub.setDuration(request.getDuration() != null ? request.getDuration() : 12);
        sub.setCommunityId(request.getCommunityId());
        sub.setSellerId(request.getSellerId());
        sub.setAmount(request.getAmount());
        sub.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setSubscriptionCreatedDate(Instant.now());
        sub.setSubscriptionEndDate(Instant.now().plus(365, ChronoUnit.DAYS));
        sub.setLastModifiedTimestamp(Instant.now());
        sub.setNotifyOnCheckouts(request.isNotifyOnCheckouts());
        sub.setRemind(request.isRemind());

        ProductSubscription saved = subscriptionRepository.save(sub);
        return toResponse(saved);
    }

    public List<ProductSubscriptionResponse> getCustomerSubscriptions(UUID customerId) {
        return subscriptionRepository.findByCustomerId(customerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void cancelSubscription(UUID subscriptionId) {
        ProductSubscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));
        sub.setStatus(SubscriptionStatus.INACTIVE);
        sub.setLastModifiedTimestamp(Instant.now());
        subscriptionRepository.save(sub);
    }

    private ProductSubscriptionResponse toResponse(ProductSubscription s) {
        ProductSubscriptionResponse r = new ProductSubscriptionResponse();
        r.setId(s.getId());
        r.setCustomerId(s.getCustomerId());
        r.setProductId(s.getProductId());
        r.setVariantId(s.getProductVariantId());
        r.setQuantity(s.getQuantity());
        r.setFrequency(s.getSubscriptionCalendarUnit());
        r.setDuration(s.getDuration());
        r.setCommunityId(s.getCommunityId());
        r.setSellerId(s.getSellerId());
        r.setAmount(s.getAmount());
        r.setCurrency(s.getCurrency());
        r.setStatus(s.getStatus());
        r.setSubscriptionCreatedDate(s.getSubscriptionCreatedDate());
        r.setSubscriptionEndDate(s.getSubscriptionEndDate());
        r.setNotifyOnCheckouts(s.isNotifyOnCheckouts());
        r.setRemind(s.isRemind());
        return r;
    }
}
