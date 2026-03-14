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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
}