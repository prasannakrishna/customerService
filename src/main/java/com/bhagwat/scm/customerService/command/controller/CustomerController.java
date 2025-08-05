package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.commanddto.CreateCustomerCommand;
import com.bhagwat.scm.customerService.command.commanddto.CreateOrderCommand;
import com.bhagwat.scm.customerService.dto.CustomerRequest;
import com.bhagwat.scm.customerService.command.entity.Customer;
import com.bhagwat.scm.customerService.query.dto.FindCustomerById;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final QueryGateway queryGateway;
    private final CommandGateway commandGateway;

    public CustomerController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PutMapping("/{id}")
    public CompletableFuture<Void> updateCustomer(@PathVariable String id, @RequestBody CreateOrderCommand.UpdateCustomerCommand command) {
        return commandGateway.send(command);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<Void> deleteCustomer(@PathVariable String id) {
        return commandGateway.send(new CreateOrderCommand.DeleteCustomerCommand(id));
    }

    @GetMapping("/{id}")
    public Optional<Customer> getCustomer(@PathVariable Long id) {
        return queryGateway.query(
                new FindCustomerById(id),
                ResponseTypes.optionalInstanceOf(Customer.class)
        ).join();
    }


    @PostMapping
    public CompletableFuture<String> createCustomer(@RequestBody CustomerRequest request) {
        String createId = UUID.randomUUID().toString();

        return commandGateway.send(new CreateCustomerCommand(
                createId,
                request.getFname(),
                request.getMname(),
                request.getLname(),
                request.getEmail(),
                request.getMobileNumber(),
                request.isEmailVerified(),
                request.isMobileVerified(),
                request.getAddresses()
        ));
    }
}

