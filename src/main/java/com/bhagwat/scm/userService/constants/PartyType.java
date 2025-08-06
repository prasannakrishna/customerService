package com.bhagwat.scm.userService.constants;

public enum PartyType {
    SELLER,
    CARRIER,
    LOGISTICS_PROVIDER, // Renamed for clarity from 'logistic provider'
    SUPPLIER,
    WHOLESALER,
    DISTRIBUTOR,
    WAREHOUSE_SERVICE_PROVIDER, // Renamed for clarity from 'warehouse service provider'
    RETAILER,
    FREIGHT_FORWARDER;

    // You can add additional methods or properties here if needed,
    // for example, a display name:
    public String getDisplayName() {
        return this.name().replace("_", " ").toLowerCase();
    }
}