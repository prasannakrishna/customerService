package com.bhagwat.scm.customerService.query.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_view")
public class CustomerView {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileNumber;
    private boolean isEmailVerified;
    private boolean isMobileVerified;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AddressView> addresses;

}
