package com.bhagwat.scm.customerService.command.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Data
@Table(name = "address")
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID addressId;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String post;
    private String pincode;
    private String state;
    private String country;
    private String landMark;
    private double longitude;
    private double latitude;
    private boolean isPrimaryAddress;

    /** HOME, WORK, OFFICE, OTHER */
    @Column(name = "address_type", length = 20)
    private String addressType;

    /** User-friendly label: "Home", "Mom's place", "Office" */
    @Column(name = "address_label", length = 50)
    private String addressLabel;

    @Column(name = "contact_phone", length = 15)
    private String contactPhone;

    // ── Google Maps location data ───────────────────────────────────────

    /** Google Maps Place ID — unique identifier, can reconstruct full address */
    @Column(name = "place_id", length = 100)
    private String placeId;

    /** Google Plus Code — works even in areas without formal addresses */
    @Column(name = "plus_code", length = 20)
    private String plusCode;

    /** Full formatted address from Google Maps */
    @Column(name = "formatted_address", columnDefinition = "TEXT")
    private String formattedAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
}