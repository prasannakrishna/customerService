package com.bhagwat.scm.userService.controller;

import com.bhagwat.scm.userService.dto.UserCreationRequest;
import com.bhagwat.scm.userService.dto.UserCreationResponse;
import com.bhagwat.scm.userService.dto.UserLoginRequest;
import com.bhagwat.scm.userService.dto.UserLoginResponse;
import com.bhagwat.scm.userService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/validate")
    public ResponseEntity<UserLoginResponse> validateUser(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.validateUserAndGetAppAccess(request.getUsername(), request.getPassword());
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<UserCreationResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        UserCreationResponse response = userService.createUser(request);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}