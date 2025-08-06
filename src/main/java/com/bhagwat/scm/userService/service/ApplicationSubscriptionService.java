package com.bhagwat.scm.userService.service;

import com.bhagwat.scm.userService.constants.SubscriptionPlan;
import com.bhagwat.scm.userService.dto.SubscriptionRequest;
import com.bhagwat.scm.userService.dto.SubscriptionResponse;
import com.bhagwat.scm.userService.entity.ApplicationSubscription;
import com.bhagwat.scm.userService.entity.Org;
import com.bhagwat.scm.userService.repository.ApplicationSubscriptionRepository;
import com.bhagwat.scm.userService.repository.OrgRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationSubscriptionService {

    private final ApplicationSubscriptionRepository subscriptionRepository;
    private final OrgRepository orgRepository;

    public ApplicationSubscriptionService(ApplicationSubscriptionRepository subscriptionRepository, OrgRepository orgRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.orgRepository = orgRepository;
    }

    @Transactional
    public SubscriptionResponse addOrUpdateSubscription(SubscriptionRequest request) {
        Org org = orgRepository.findById(request.getOrgId())
                .orElse(null);

        if (org == null) {
            return new SubscriptionResponse("Organization not found.", false, null);
        }

        Optional<ApplicationSubscription> existingSubOptional = org.getSubscriptions().stream()
                .filter(s -> s.getAppId() == request.getAppId())
                .findFirst();

        ApplicationSubscription subscription;
        if (existingSubOptional.isPresent()) {
            subscription = existingSubOptional.get();
            subscription.setPlan(request.getPlan());
            subscription.setSubscriptionKey(request.getSubscriptionKey());
            subscription.setIsActive(true);
            if (request.getRenewalDate() != null) {
                subscription.setRenewalDate(request.getRenewalDate());
            }
        } else {
            subscription = new ApplicationSubscription(
                    UUID.randomUUID().toString(),
                    org,
                    request.getAppId(),
                    request.getSubscriptionKey(),
                    request.getPlan()
            );
            if (request.getRenewalDate() != null && request.getPlan() != SubscriptionPlan.TRIAL) {
                subscription.setRenewalDate(request.getRenewalDate());
            }
            org.addSubscription(subscription);
        }

        ApplicationSubscription savedSubscription = subscriptionRepository.save(subscription);
        return new SubscriptionResponse("Subscription added/updated successfully.", true, savedSubscription.getAppSubId());
    }

    @Transactional
    public SubscriptionResponse deactivateSubscription(String appSubId) {
        Optional<ApplicationSubscription> subscriptionOptional = subscriptionRepository.findById(appSubId);
        if (subscriptionOptional.isEmpty()) {
            return new SubscriptionResponse("Subscription not found.", false, null);
        }
        ApplicationSubscription subscription = subscriptionOptional.get();
        subscription.setIsActive(false);
        subscriptionRepository.save(subscription);
        return new SubscriptionResponse("Subscription deactivated successfully.", true, subscription.getAppSubId());
    }
}