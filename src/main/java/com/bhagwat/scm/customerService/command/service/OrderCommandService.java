package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.customerService.command.entity.CustomerOrder;
import com.bhagwat.scm.customerService.command.repository.CustomerOrderRepository;
import com.bhagwat.scm.customerService.constant.CustomerOrderStatus;
import com.bhagwat.scm.customerService.constant.PaymentStatus;
import com.bhagwat.scm.customerService.dto.OrderResponse;
import com.bhagwat.scm.customerService.dto.PlaceOrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderCommandService {

    private final CustomerOrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public OrderCommandService(CustomerOrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        CustomerOrder order = new CustomerOrder();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setVariantId(request.getVariantId());
        order.setQuantity(request.getQuantity());
        order.setPricePerUnit(request.getPricePerUnit());
        order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        order.setCommunityId(request.getCommunityId());
        order.setSellerId(request.getSellerId());
        order.setInventoryKey(request.getInventoryKey() != null ? request.getInventoryKey() : "default");
        order.setShoppingBagId(request.getShoppingBagId());
        order.setOrderCreatedDate(Instant.now());
        order.setShipByDate(Instant.now().plus(3, ChronoUnit.DAYS));
        order.setDeliveryByDate(Instant.now().plus(7, ChronoUnit.DAYS));
        order.setOrderStatus(CustomerOrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        BigDecimal amount = request.getPricePerUnit().multiply(BigDecimal.valueOf(request.getQuantity()));
        order.setAmount(amount);
        order.setShippingCost(BigDecimal.valueOf(5.99));
        order.setTaxAmount(amount.multiply(BigDecimal.valueOf(0.08)));

        if (request.getShippingAddress() != null) {
            try {
                order.setCustomerAddress(objectMapper.writeValueAsString(request.getShippingAddress()));
            } catch (JsonProcessingException e) {
                order.setCustomerAddress("{}");
            }
        }

        CustomerOrder saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public List<OrderResponse> getOrdersByCustomerId(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByOrderCreatedDateDesc(customerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public OrderResponse getOrderByOrderId(UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    private OrderResponse toResponse(CustomerOrder o) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setOrderId(o.getOrderId());
        r.setCustomerId(o.getCustomerId());
        r.setProductId(o.getProductId());
        r.setVariantId(o.getVariantId());
        r.setQuantity(o.getQuantity());
        r.setPricePerUnit(o.getPricePerUnit());
        r.setAmount(o.getAmount());
        r.setShippingCost(o.getShippingCost());
        r.setTaxAmount(o.getTaxAmount());
        r.setCurrency(o.getCurrency());
        r.setOrderStatus(o.getOrderStatus());
        r.setPaymentStatus(o.getPaymentStatus());
        r.setTrackingId(o.getTrackingId());
        r.setOrderCreatedDate(o.getOrderCreatedDate());
        r.setShipByDate(o.getShipByDate());
        r.setDeliveryByDate(o.getDeliveryByDate());
        r.setDeliveryDate(o.getDeliveryDate());
        r.setCommunityId(o.getCommunityId());
        r.setSellerId(o.getSellerId());
        r.setCancellationReason(o.getCancellationReason());
        return r;
    }
}
