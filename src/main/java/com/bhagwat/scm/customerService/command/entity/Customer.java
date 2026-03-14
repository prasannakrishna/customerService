package com.bhagwat.scm.customerService.command.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Entity
@Data
@AllArgsConstructor
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"fname", "email", "mobileNumber"})
})
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String username;
    private String passwordHash;
    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileNumber;
    private boolean isEmailVerified;
    private boolean isMobileVerified;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Address> addresses;
}