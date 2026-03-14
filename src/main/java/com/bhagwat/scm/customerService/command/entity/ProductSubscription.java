package com.bhagwat.scm.customerService.command.entity;

import com.bhagwat.scm.customerService.constant.SubscriptionStatus;
import com.bhagwat.scm.inventorydto.SubscriptionCalendarUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    private SubscriptionCalendarUnit subscriptionCalendarUnit;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_variant_id")
    private UUID productVariantId;
    private Integer quantity;

    private Instant subscriptionCreatedDate;
    private Integer duration; // in terms of the calendar unit
    private Instant subscriptionEndDate;

    private UUID paymentId;
    private String communityId;

    private String sellerId;
    private BigDecimal amount;
    private String currency;
    private String communityOrderHeaderId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private Instant lastModifiedTimestamp;
    private boolean notifyOnCheckouts;
    private boolean remind;

    // Once payment is made, subscription cannot be cancelled — only held
    @Column(name = "is_paid")
    private boolean paid;

    // Holds until this timestamp; null means not on hold
    @Column(name = "hold_until")
    private Instant holdUntil;
}