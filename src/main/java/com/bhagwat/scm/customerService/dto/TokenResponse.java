package com.bhagwat.scm.customerService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data @Builder @AllArgsConstructor
public class TokenResponse {
    private UUID customerId;
    private String username;
    private String fname;
    private String lname;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String tokenType; // "Bearer" or "DPoP"
    private long expiresIn; // seconds
}
