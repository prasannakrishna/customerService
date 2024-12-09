package com.bhagwat.scm.customerService.controller;

import com.bhagwat.scm.customerService.dto.CreateCustomerCommand;
import com.bhagwat.scm.customerService.dto.DeleteCustomerCommand;
import com.bhagwat.scm.customerService.dto.UpdateCustomerCommand;
import com.bhagwat.scm.customerService.entity.CustomerEntity;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public CustomerController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public CompletableFuture<String> createCustomer(@RequestBody CreateCustomerCommand command) {
        return commandGateway.send(command);
    }

    @PutMapping("/{id}")
    public CompletableFuture<Void> updateCustomer(@PathVariable String id, @RequestBody UpdateCustomerCommand command) {
        return commandGateway.send(command);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<Void> deleteCustomer(@PathVariable String id) {
        return commandGateway.send(new DeleteCustomerCommand(id));
    }

    @GetMapping("/{id}")
    public CompletableFuture<CustomerEntity> getCustomer(@PathVariable String id) {
        return queryGateway.query("findCustomerById", id, CustomerEntity.class);
    }
}

