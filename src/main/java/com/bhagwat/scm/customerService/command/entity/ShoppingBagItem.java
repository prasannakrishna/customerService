package com.bhagwat.scm.customerService.command.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "shopping_bag_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingBagItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_bag_id", nullable = false)
    private ShoppingBag shoppingBag;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID productVariantId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}