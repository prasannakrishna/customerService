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

    public AuthController(AuthService authService) {
        this.authService = authService;
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Stateless — client clears token/session
        return ResponseEntity.ok().build();
    }
}
