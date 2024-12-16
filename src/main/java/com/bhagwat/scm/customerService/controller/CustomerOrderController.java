package com.bhagwat.scm.customerService.controller;

import com.bhagwat.scm.customerService.common.CreateOrderCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerOrderController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping("/placeOrder")
    public ResponseEntity<String> placeOrder(@RequestBody PlaceOrderRequest request) {
        String orderId = UUID.randomUUID().toString();

        // Trigger the Saga by sending the CreateOrderCommand
        commandGateway.send(new CreateOrderCommand(orderId, request.getCustomerId(), request.getOrderLines()));

        return ResponseEntity.ok("Order placed successfully. Order ID: " + orderId);
    }
}
