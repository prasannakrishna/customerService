package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.customerService.command.entity.ProductSubscription;
import com.bhagwat.scm.customerService.command.repository.ProductSubscriptionRepository;
import com.bhagwat.scm.customerService.constant.SubscriptionStatus;
import com.bhagwat.scm.customerService.dto.AddProductSubscriptionRequest;
import com.bhagwat.scm.customerService.dto.ProductSubscriptionResponse;
import com.bhagwat.scm.customerService.dto.SubscriptionPlanDto;
import com.bhagwat.scm.inventorydto.SubscriptionCalendarUnit;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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
                new SubscriptionPlanDto("free",     "Free",     BigDecimal.ZERO,           "monthly", "Basic access, no perks"),
                new SubscriptionPlanDto("silver",   "Silver",   new BigDecimal("9.99"),    "monthly", "Priority support & early access"),
                new SubscriptionPlanDto("gold",     "Gold",     new BigDecimal("19.99"),   "monthly", "All Silver perks + exclusive deals"),
                new SubscriptionPlanDto("platinum", "Platinum", new BigDecimal("39.99"),   "monthly", "All Gold perks + free shipping on every order")
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
        sub.setPaid(false); // payment not yet confirmed

        return toResponse(subscriptionRepository.save(sub));
    }

    /**
     * Mark subscription as paid. Once paid, it can no longer be cancelled — only held.
     */
    @Transactional
    public ProductSubscriptionResponse confirmPayment(UUID subscriptionId) {
        ProductSubscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));
        sub.setPaid(true);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setLastModifiedTimestamp(Instant.now());
        return toResponse(subscriptionRepository.save(sub));
    }

    /**
     * Hold a subscription for the current cycle.
     * - DAILY  → hold until end of today (midnight UTC)
     * - WEEKLY / BIWEEKLY → hold until end of current week (Sunday midnight UTC)
     * - MONTHLY / QUARTERLY / YEARLY → hold until end of current month
     * Cannot hold an already-held subscription.
     */
    @Transactional
    public ProductSubscriptionResponse holdSubscription(UUID subscriptionId) {
        ProductSubscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        if (sub.getStatus() == SubscriptionStatus.HOLD) {
            throw new IllegalStateException("Subscription is already on hold until " + sub.getHoldUntil());
        }
        if (sub.getStatus() == SubscriptionStatus.INACTIVE) {
            throw new IllegalStateException("Subscription is not active.");
        }

        Instant holdUntil = computeHoldUntil(sub.getSubscriptionCalendarUnit());
        sub.setStatus(SubscriptionStatus.HOLD);
        sub.setHoldUntil(holdUntil);
        sub.setLastModifiedTimestamp(Instant.now());
        return toResponse(subscriptionRepository.save(sub));
    }

    /**
     * Resume a held subscription back to ACTIVE.
     */
    @Transactional
    public ProductSubscriptionResponse resumeSubscription(UUID subscriptionId) {
        ProductSubscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));
        if (sub.getStatus() != SubscriptionStatus.HOLD) {
            throw new IllegalStateException("Subscription is not on hold.");
        }
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setHoldUntil(null);
        sub.setLastModifiedTimestamp(Instant.now());
        return toResponse(subscriptionRepository.save(sub));
    }

    /**
     * Cancel is only allowed before payment. After payment, throws an exception.
     */
    @Transactional
    public void cancelSubscription(UUID subscriptionId) {
        ProductSubscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));
        if (sub.isPaid()) {
            throw new IllegalStateException(
                "Subscription cannot be cancelled after payment. You can put it on hold instead.");
        }
        sub.setStatus(SubscriptionStatus.INACTIVE);
        sub.setLastModifiedTimestamp(Instant.now());
        subscriptionRepository.save(sub);
    }

    public List<ProductSubscriptionResponse> getCustomerSubscriptions(UUID customerId) {
        return subscriptionRepository.findByCustomerId(customerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Hold-until calculation ────────────────────────────────────────────────

    private Instant computeHoldUntil(SubscriptionCalendarUnit unit) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        if (unit == null) {
            return now.toLocalDate().atStartOfDay(ZoneOffset.UTC).plusDays(1).toInstant();
        }
        return switch (unit) {
            case DAY ->
                // end of today midnight UTC
                now.toLocalDate().atStartOfDay(ZoneOffset.UTC).plusDays(1).toInstant();
            case WEEK, BIWEEKLY ->
                // end of current week (next Monday midnight UTC)
                now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                   .toLocalDate().atStartOfDay(ZoneOffset.UTC).plusDays(1).toInstant();
            default ->
                // end of current month
                now.with(TemporalAdjusters.lastDayOfMonth())
                   .toLocalDate().atStartOfDay(ZoneOffset.UTC).plusDays(1).toInstant();
        };
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

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
        r.setPaid(s.isPaid());
        r.setHoldUntil(s.getHoldUntil());
        r.setCancellable(!s.isPaid());
        return r;
    }
}
