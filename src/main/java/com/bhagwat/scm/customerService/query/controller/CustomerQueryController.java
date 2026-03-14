package com.bhagwat.scm.customerService.query.controller;
import com.bhagwat.scm.customerService.command.service.CustomerCommandService;
import com.bhagwat.scm.customerService.dto.CustomerDto;
import com.bhagwat.scm.customerService.dto.UpdateCustomerRequest;
import com.bhagwat.scm.customerService.query.service.CustomerQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerQueryController {

    private final CustomerQueryService customerQueryService;
    private final CustomerCommandService customerCommandService;

    public CustomerQueryController(CustomerQueryService customerQueryService,
                                   CustomerCommandService customerCommandService) {
        this.customerQueryService = customerQueryService;
        this.customerCommandService = customerCommandService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = customerQueryService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable UUID id) {
        CustomerDto customer = customerQueryService.getCustomerById(id);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<CustomerDto> getByUsername(@PathVariable String username) {
        CustomerDto customer = customerQueryService.getCustomerByUsername(username);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/by-username/{username}")
    public ResponseEntity<CustomerDto> updateByUsername(@PathVariable String username,
                                                         @RequestBody UpdateCustomerRequest request) {
        CustomerDto existing = customerQueryService.getCustomerByUsername(username);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        request.setId(existing.getId());
        CustomerDto updated = customerCommandService.updateCustomer(existing.getId(), request);
        return ResponseEntity.ok(updated);
    }
}
