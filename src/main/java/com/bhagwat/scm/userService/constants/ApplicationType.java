package com.bhagwat.scm.userService.constants;

public enum ApplicationType {
    SELLER_APP("Seller App", "[https://seller.example.com/](https://seller.example.com/)"),
    LOGISTIC_APP("Logistic App", "[https://logistic.example.com/](https://logistic.example.com/)"),
    STORE_APP("Store App", "[https://store.example.com/](https://store.example.com/)"),
    SITE_APP("Site App", "[https://site.example.com/](https://site.example.com/)");

    private final String displayName;
    private final String baseUrl;

    ApplicationType(String displayName, String baseUrl) {
        this.displayName = displayName;
        this.baseUrl = baseUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}