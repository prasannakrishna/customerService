package com.bhagwat.scm.customerService.saga;

import org.axonframework.spring.stereotype.Saga;

//@Saga
public class CustomerOrderSaga {}
/*

   @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    private boolean inventoryAllocated = false;
    private boolean paymentSuccessful = false;

    *//**
     * Saga starts when an OrderPlacedEvent is published.
     *//*
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderPlacedEvent event) {
        this.orderId = event.getOrderId();

        // Trigger order creation in the Order Service
        SagaLifecycle.associateWith("orderId", event.getOrderId());
        commandGateway.send(new CreateOrderCommand(event.getOrderId(), event.getCustomerId(), event.getOrderLines()));
    }

    *//**
     * Handles the OrderCreatedEvent emitted by the Order Service.
     *//*
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        // Trigger inventory allocation
        commandGateway.send(new AllocateInventoryCommand(event.getOrderId(), event.getOrderLines()));
    }

    *//**
     * Handles the InventoryAllocatedEvent emitted by the Inventory Service.
     *//*
    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryAllocatedEvent event) {
        this.inventoryAllocated = true;
       // String paymentServiceUrl = discoveryClient.getInstances("payment-service").get(0).getUri().toString();
        commandGateway.send(new MakePaymentCommand(event.getOrderId(), paymentServiceUrl));

        // Trigger payment processing
        //commandGateway.send(new MakePaymentCommand(event.getOrderId(), event.getTotalAmount()));
    }

    *//**
     * Handles the InventoryAllocationFailedEvent emitted by the Inventory Service.
     *//*
    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryAllocationFailedEvent event) {
        // Rollback order creation and end the saga
        SagaLifecycle.end();
        System.out.println("Inventory allocation failed. Order canceled.");
    }

    *//*
     * Handles the PaymentSuccessfulEvent emitted by the Payment Service.
     *//*
    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentProcessedEvent event) {
        this.paymentSuccessful = true;

        // Trigger carrier assignment
        commandGateway.send(new AssignCarrierCommand(event.getOrderId(), event.getDeliveryAddress()));
    }

    *//**
     * Handles the PaymentFailedEvent emitted by the Payment Service.
     *//*
    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentFailedEvent event) {
        // Rollback inventory allocation and end the saga
        SagaLifecycle.end();
        System.out.println("Payment failed. Order canceled.");
    }

    *//**
     * Handles the CarrierAssignedEvent emitted by the Carrier Service.
     *//*
    @SagaEventHandler(associationProperty = "orderId")
    public void on(CarrierAssignedEvent event) {
        if (paymentSuccessful) {
            // Trigger shipment creation
            commandGateway.send(new CreateShipmentCommand(event.getOrderId(), event.getConsignmentDetails()));
        } else {
            // Rollback carrier assignment and end the saga
            SagaLifecycle.end();
            System.out.println("Payment not completed. Carrier assignment rolled back.");
        }
    }

    *//*
     Handles the ShipmentCreatedEvent emitted by the Shipment Service.
     *//*
    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShipmentCreatedEvent event) {
        // Complete the saga
        SagaLifecycle.end();
        System.out.println("Order workflow completed. Shipment created.");
    }
}*/
