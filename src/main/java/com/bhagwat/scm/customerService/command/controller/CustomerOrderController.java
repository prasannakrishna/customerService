package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.service.OrderCommandService;
import com.bhagwat.scm.customerService.dto.OrderResponse;
import com.bhagwat.scm.customerService.dto.PlaceOrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class CustomerOrderController {

    private final OrderCommandService orderCommandService;

    public CustomerOrderController(OrderCommandService orderCommandService) {
        this.orderCommandService = orderCommandService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderCommandService.placeOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestParam UUID customerId) {
        return ResponseEntity.ok(orderCommandService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderCommandService.getOrderByOrderId(orderId));
    }
}
