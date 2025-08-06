package com.bhagwat.scm.userService.dto;

public class AppAccessLink {
    private String appId;
    private String appUrl;
    private String tenantId;

    public AppAccessLink(String appId, String appUrl, String tenantId) {
        this.appId = appId;
        this.appUrl = appUrl;
        this.tenantId = tenantId;
    }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getAppUrl() { return appUrl; }
    public void setAppUrl(String appUrl) { this.appUrl = appUrl; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}