package com.bhagwat.scm.customerService.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    private String id;
    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileNumber;

    @Embedded
    private Address address;
}

