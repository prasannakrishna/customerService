package com.bhagwat.scm.customerService.query.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class CustomerDocument {

    @Id
    private String id;
    private String username;
    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileNumber;
    private boolean isEmailVerified;
    private boolean isMobileVerified;
    private List<EmbeddedAddress> addresses;

    // Nested class to represent the embedded address.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmbeddedAddress {
        private String addressId;
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
    }
}