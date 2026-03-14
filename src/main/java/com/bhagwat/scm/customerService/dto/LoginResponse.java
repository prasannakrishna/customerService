package com.bhagwat.scm.customerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponse {
    private UUID customerId;
    private String username;
    private String fname;
    private String lname;
    private String email;
}
