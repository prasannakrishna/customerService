package com.bhagwat.scm.userService.controller;

import com.bhagwat.scm.userService.dto.RoleCreationRequest;
import com.bhagwat.scm.userService.dto.RoleCreationResponse;
import com.bhagwat.scm.userService.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
    public ResponseEntity<RoleCreationResponse> createRole(@Valid @RequestBody RoleCreationRequest request) {
        RoleCreationResponse response = roleService.createRole(request);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
}