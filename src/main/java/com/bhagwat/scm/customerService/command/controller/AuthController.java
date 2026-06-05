package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.service.AuthService;
import com.bhagwat.scm.customerService.dto.CreateCustomerRequest;
import com.bhagwat.scm.customerService.dto.LoginRequest;
import com.bhagwat.scm.customerService.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final AuthService authService;
    private final com.bhagwat.scm.customerService.command.service.JwtService jwtService;
    private final com.bhagwat.scm.customerService.command.service.PasswordResetService resetService;

    public AuthController(AuthService authService,
                          com.bhagwat.scm.customerService.command.service.JwtService jwtService,
                          com.bhagwat.scm.customerService.command.service.PasswordResetService resetService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.resetService = resetService;
    }

    @PostMapping("/create")
    public ResponseEntity<LoginResponse> register(@RequestBody CreateCustomerRequest request) {
        LoginResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/validate")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /** Request OTP for mobile login */
    @PostMapping("/otp/request")
    public ResponseEntity<java.util.Map<String, Object>> requestOtp(@RequestBody java.util.Map<String, String> body) {
        String mobile = body.get("mobileNumber");
        authService.sendOtp(mobile);
        return ResponseEntity.ok(java.util.Map.of("success", true, "message", "OTP sent to " + mobile));
    }

    /** Verify OTP and login */
    @PostMapping("/otp/verify")
    public ResponseEntity<LoginResponse> verifyOtp(@RequestBody java.util.Map<String, String> body) {
        String mobile = body.get("mobileNumber");
        String otp = body.get("otp");
        LoginResponse response = authService.loginWithOtp(mobile, otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    // ── JWT Token Login (returns access + refresh tokens) ────────────────

    @PostMapping("/token")
    public ResponseEntity<com.bhagwat.scm.customerService.dto.TokenResponse> loginWithToken(
            @RequestBody LoginRequest request,
            @RequestHeader(value = "DPoP", required = false) String dpopProof,
            jakarta.servlet.http.HttpServletRequest httpRequest) {

        LoginResponse login = authService.login(request);

        // If DPoP proof provided, bind token to client's key
        String dpopJkt = null;
        String tokenType = "Bearer";
        if (dpopProof != null && !dpopProof.isBlank()) {
            String method = httpRequest.getMethod();
            String uri = httpRequest.getRequestURL().toString();
            dpopJkt = jwtService.validateDpopProof(dpopProof, method, uri);
            tokenType = "DPoP";
        }

        String accessToken = jwtService.generateAccessToken(login.getCustomerId(), login.getUsername(), login.getEmail(), dpopJkt);
        String refreshToken = jwtService.generateRefreshToken(login.getCustomerId());

        return ResponseEntity.ok(com.bhagwat.scm.customerService.dto.TokenResponse.builder()
                .customerId(login.getCustomerId())
                .username(login.getUsername())
                .fname(login.getFname())
                .lname(login.getLname())
                .email(login.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .expiresIn(3600)
                .build());
    }

    // ── Forgot Password (Magic Link) ────────────────────────────────────

    @PostMapping("/forgot-password")
    public ResponseEntity<java.util.Map<String, Object>> forgotPassword(@RequestBody java.util.Map<String, String> body) {
        String email = body.get("email");
        return ResponseEntity.ok(resetService.requestPasswordReset(email));
    }

    @GetMapping("/reset-password/validate")
    public ResponseEntity<java.util.Map<String, Object>> validateResetToken(@RequestParam String token) {
        return ResponseEntity.ok(resetService.validateResetToken(token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<java.util.Map<String, Object>> resetPassword(@RequestBody java.util.Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        String confirmPassword = body.get("confirmPassword");
        return ResponseEntity.ok(resetService.resetPassword(token, newPassword, confirmPassword));
    }
}
