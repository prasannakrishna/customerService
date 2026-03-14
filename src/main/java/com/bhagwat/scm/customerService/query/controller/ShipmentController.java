package com.bhagwat.scm.customerService.query.controller;

import com.bhagwat.scm.customerService.command.entity.CustomerOrder;
import com.bhagwat.scm.customerService.command.repository.CustomerOrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final CustomerOrderRepository orderRepository;

    public ShipmentController(CustomerOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<Map<String, Object>> trackShipment(@PathVariable String trackingId) {
        return orderRepository.findAll().stream()
                .filter(o -> trackingId.equals(o.getTrackingId()))
                .findFirst()
                .map(o -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("trackingId", o.getTrackingId());
                    info.put("orderId", o.getOrderId());
                    info.put("orderStatus", o.getOrderStatus());
                    info.put("shipByDate", o.getShipByDate());
                    info.put("deliveryByDate", o.getDeliveryByDate());
                    info.put("deliveryDate", o.getDeliveryDate());
                    return ResponseEntity.ok(info);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getShipmentByOrderId(@RequestParam UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(o -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("trackingId", o.getTrackingId());
                    info.put("orderId", o.getOrderId());
                    info.put("orderStatus", o.getOrderStatus());
                    info.put("shipByDate", o.getShipByDate());
                    info.put("deliveryByDate", o.getDeliveryByDate());
                    info.put("deliveryDate", o.getDeliveryDate());
                    return ResponseEntity.ok(info);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
