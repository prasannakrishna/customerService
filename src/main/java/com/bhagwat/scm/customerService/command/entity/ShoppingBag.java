package com.bhagwat.scm.customerService.command.entity;

import com.bhagwat.scm.customerService.constant.ShoppingBagStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shopping_bags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingBag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShoppingBagStatus status;

    @Column(name = "total_item_count")
    private Integer totalItemCount;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "customer_order_id", unique = true)
    private UUID customerOrderId;

    @OneToMany(mappedBy = "shoppingBag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ShoppingBagItem> items;

    @Column(name = "created_timestamp", nullable = false)
    private Instant createdTimestamp;

    @Column(name = "last_updated_timestamp", nullable = false)
    private Instant lastUpdatedTimestamp;
}