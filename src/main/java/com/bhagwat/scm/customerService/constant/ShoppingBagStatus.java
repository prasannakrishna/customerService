package com.bhagwat.scm.customerService.constant;

public enum ShoppingBagStatus {
    CREATED,        // The bag has been created but no items have been added yet
    FILLING,        // Items are being added to the bag
    PAYMENT_PENDING, // The user is in the process of making a payment
    CHECKED_OUT,    // Payment is complete, and the bag has been converted into an order
    ABANDONED
}
