package com.bhagwat.scm.userService.entity;

import com.bhagwat.scm.userService.constants.ApplicationType;
import com.bhagwat.scm.userService.constants.SubscriptionPlan;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "application_subscriptions")
public class ApplicationSubscription {

    @Id
    @Column(name = "app_sub_id", unique = true, nullable = false, length = 50)
    private String appSubId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", referencedColumnName = "org_id", nullable = false)
    private Org org;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_id", nullable = false, length = 50)
    private ApplicationType appId;

    @Column(name = "subscription_key", nullable = false, length = 255)
    private String subscriptionKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 50)
    private SubscriptionPlan plan;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "renewal_date", nullable = false)
    private LocalDate renewalDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public ApplicationSubscription() {}

    public ApplicationSubscription(String appSubId, Org org, ApplicationType appId, String subscriptionKey, SubscriptionPlan plan) {
        this.appSubId = appSubId;
        this.org = org;
        this.appId = appId;
        this.subscriptionKey = subscriptionKey;
        this.plan = plan;
        this.createdAt = LocalDateTime.now();
        setRenewalDateBasedOnPlan();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        setRenewalDateBasedOnPlan();
    }

    private void setRenewalDateBasedOnPlan() {
        if (this.plan == SubscriptionPlan.TRIAL) {
            this.renewalDate = this.createdAt.toLocalDate().plusDays(15);
        }
        if (this.renewalDate == null && this.plan != SubscriptionPlan.TRIAL) {
            this.renewalDate = this.createdAt.toLocalDate().plusYears(1); // Default for non-trial
        }
    }

    public String getAppSubId() { return appSubId; }
    public void setAppSubId(String appSubId) { this.appSubId = appSubId; }
    public Org getOrg() { return org; }
    public void setOrg(Org org) { this.org = org; }
    public ApplicationType getAppId() { return appId; }
    public void setAppId(ApplicationType appId) { this.appId = appId; }
    public String getSubscriptionKey() { return subscriptionKey; }
    public void setSubscriptionKey(String subscriptionKey) { this.subscriptionKey = subscriptionKey; }
    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
        setRenewalDateBasedOnPlan();
    }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDate getRenewalDate() { return renewalDate; }
    public void setRenewalDate(LocalDate renewalDate) { this.renewalDate = renewalDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationSubscription that = (ApplicationSubscription) o;
        return Objects.equals(appSubId, that.appSubId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appSubId);
    }

    @Override
    public String toString() {
        return "ApplicationSubscription{" +
                "appSubId='" + appSubId + '\'' +
                ", appId=" + appId +
                ", plan=" + plan +
                ", renewalDate=" + renewalDate +
                '}';
    }
}