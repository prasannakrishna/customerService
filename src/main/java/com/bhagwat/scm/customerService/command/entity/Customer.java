package com.bhagwat.scm.customerService.command.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
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

    // ── Profile fields ──────────────────────────────────────────────────

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @Column(name = "default_latitude")
    private Double defaultLatitude;

    @Column(name = "default_longitude")
    private Double defaultLongitude;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Address> addresses;
}