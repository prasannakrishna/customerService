package com.bhagwat.scm.customerService.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class CustomerEntity {
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

