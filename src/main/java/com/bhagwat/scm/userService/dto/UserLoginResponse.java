package com.bhagwat.scm.userService.dto;

import java.util.List;

public class UserLoginResponse {
    private String message;
    private boolean success;
    private List<AppAccessLink> appAccessLinks;

    public UserLoginResponse(String message, boolean success, List<AppAccessLink> appAccessLinks) {
        this.message = message;
        this.success = success;
        this.appAccessLinks = appAccessLinks;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public List<AppAccessLink> getAppAccessLinks() { return appAccessLinks; }
    public void setAppAccessLinks(List<AppAccessLink> appAccessLinks) { this.appAccessLinks = appAccessLinks; }
}