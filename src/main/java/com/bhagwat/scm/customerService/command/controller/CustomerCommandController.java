package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.service.CustomerCommandService;
import com.bhagwat.scm.customerService.dto.CreateCustomerRequest;
import com.bhagwat.scm.customerService.dto.CustomerDto;
import com.bhagwat.scm.customerService.dto.UpdateCustomerRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerCommandController {

    private final CustomerCommandService customerCommandService;

    public CustomerCommandController(CustomerCommandService customerCommandService) {
        this.customerCommandService = customerCommandService;
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CreateCustomerRequest request) {
        CustomerDto createdCustomer = customerCommandService.createCustomer(request);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable UUID id, @RequestBody UpdateCustomerRequest request) {
        request.setId(id);
        CustomerDto updatedCustomer = customerCommandService.updateCustomer(id, request);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerCommandService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}