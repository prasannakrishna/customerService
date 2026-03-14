package com.bhagwat.scm.customerService.command.entity;

import com.bhagwat.scm.customerService.constant.CustomerOrderStatus;
import com.bhagwat.scm.customerService.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", unique = true, nullable = false)
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // Use a separate Address entity for a proper relationship
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "customer_address_id")
    // private Address customerAddress;
    // For simplicity, we'll store the address as a JSONB string or in separate fields
    @Column(name = "customer_address_json", columnDefinition = "jsonb")
    private String customerAddress;

    @Column(name = "order_created_date", nullable = false)
    private Instant orderCreatedDate;

    @Column(name = "ship_by_date")
    private Instant shipByDate;

    @Column(name = "delivery_by_date")
    private Instant deliveryByDate;

    @Column(name = "delivery_date")
    private Instant deliveryDate;

    @Column(name = "shopping_bag_id")
    private UUID shoppingBagId;

    @Column(name = "tracking_id")
    private String trackingId;

    @Column(name = "shipping_label")
    private String shippingLabel;

    @Column(name = "inventory_key", nullable = false)
    private String inventoryKey;

    @Column(name = "tag_id")
    private UUID tagId;

    @Column(name = "community_id", nullable = false)
    private UUID communityId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private CustomerOrderStatus orderStatus;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "price_per_unit", precision = 19, scale = 2, nullable = false)
    private BigDecimal pricePerUnit;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "shipping_cost", precision = 19, scale = 2)
    private BigDecimal shippingCost;

    @Column(name = "tax_amount", precision = 19, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "cancellation_reason")
    private String cancellationReason;
}