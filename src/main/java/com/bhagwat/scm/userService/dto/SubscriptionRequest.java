package com.bhagwat.scm.userService.dto;

import com.bhagwat.scm.userService.constants.ApplicationType;
import com.bhagwat.scm.userService.constants.SubscriptionPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class SubscriptionRequest {
    @NotBlank
    private String orgId;
    @NotNull
    private ApplicationType appId;
    @NotBlank
    private String subscriptionKey;
    @NotNull
    private SubscriptionPlan plan;
    private LocalDate renewalDate;

    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public ApplicationType getAppId() { return appId; }
    public void setAppId(ApplicationType appId) { this.appId = appId; }
    public String getSubscriptionKey() { return subscriptionKey; }
    public void setSubscriptionKey(String subscriptionKey) { this.subscriptionKey = subscriptionKey; }
    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) { this.plan = plan; }
    public LocalDate getRenewalDate() { return renewalDate; }
    public void setRenewalDate(LocalDate renewalDate) { this.renewalDate = renewalDate; }
}