package com.bhagwat.scm.userService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "organizations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Org {

    @Id
    @Column(name = "org_id", unique = true, nullable = false, length = 50)
    private String orgId;

    @Column(name = "org_name", nullable = false, length = 255)
    private String orgName;

    @Column(name = "org_type", length = 100)
    private String orgType;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "tax_id", length = 100)
    private String taxId;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Column(name = "industry_type", length = 100)
    private String industryType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "org", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Division> divisions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "org_network_members",
            joinColumns = @JoinColumn(name = "org_id"),
            inverseJoinColumns = @JoinColumn(name = "network_id")
    )
    private Set<OrgNetwork> networks = new HashSet<>();

    @OneToMany(mappedBy = "org", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ApplicationSubscription> subscriptions = new HashSet<>();

    @OneToMany(mappedBy = "org", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getOrgType() { return orgType; }
    public void setOrgType(String orgType) { this.orgType = orgType; }
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStateProvince() { return stateProvince; }
    public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getIndustryType() { return industryType; }
    public void setIndustryType(String industryType) { this.industryType = industryType; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<Division> getDivisions() { return divisions; }
    public void setDivisions(Set<Division> divisions) {
        this.divisions.clear();
        if (divisions != null) this.divisions.addAll(divisions);
    }
    public Set<OrgNetwork> getNetworks() { return networks; }
    public void setNetworks(Set<OrgNetwork> networks) {
        this.networks.clear();
        if (networks != null) this.networks.addAll(networks);
    }
    public Set<ApplicationSubscription> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(Set<ApplicationSubscription> subscriptions) {
        this.subscriptions.clear();
        if (subscriptions != null) this.subscriptions.addAll(subscriptions);
    }
    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) {
        this.users.clear();
        if (users != null) this.users.addAll(users);
    }
    public void addDivision(Division division) {
        this.divisions.add(division);
        division.setOrg(this);
    }
    public void removeDivision(Division division) {
        this.divisions.remove(division);
        division.setOrg(null);
    }
    public void addNetwork(OrgNetwork network) {
        this.networks.add(network);
        network.getOrganizations().add(this);
    }
    public void removeNetwork(OrgNetwork network) {
        this.networks.remove(network);
        network.getOrganizations().remove(this);
    }
    public void addSubscription(ApplicationSubscription subscription) {
        this.subscriptions.add(subscription);
        subscription.setOrg(this);
    }
    public void removeSubscription(ApplicationSubscription subscription) {
        this.subscriptions.remove(subscription);
        subscription.setOrg(null);
    }
    public void addUser(User user) {
        this.users.add(user);
        user.setOrg(this);
    }
    public void removeUser(User user) {
        this.users.remove(user);
        user.setOrg(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Org org = (Org) o;
        return Objects.equals(orgId, org.orgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgId);
    }

    @Override
    public String toString() {
        return "Org{" +
                "orgId='" + orgId + '\'' +
                ", orgName='" + orgName + '\'' +
                ", orgType='" + orgType + '\'' +
                '}';
    }
}
