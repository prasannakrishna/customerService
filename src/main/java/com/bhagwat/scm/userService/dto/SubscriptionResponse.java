package com.bhagwat.scm.userService.dto;

public class SubscriptionResponse {
    private String message;
    private boolean success;
    private String appSubId;

    public SubscriptionResponse(String message, boolean success, String appSubId) {
        this.message = message;
        this.success = success;
        this.appSubId = appSubId;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getAppSubId() { return appSubId; }
    public void setAppSubId(String appSubId) { this.appSubId = appSubId; }
}
