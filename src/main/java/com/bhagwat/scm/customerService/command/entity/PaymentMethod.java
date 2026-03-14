package com.bhagwat.scm.customerService.command.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "payment_methods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "type", nullable = false)
    private String type;       // CREDIT_CARD, DEBIT_CARD, PAYPAL, UPI

    @Column(name = "label")
    private String label;

    @Column(name = "last_four")
    private String lastFour;

    @Column(name = "expiry_month")
    private String expiryMonth;

    @Column(name = "expiry_year")
    private String expiryYear;

    @Column(name = "is_default")
    private boolean isDefault;
}
