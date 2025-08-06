package com.bhagwat.scm.userService.dto;

public class UserCreationResponse {
    private String message;
    private boolean success;
    private String userId;

    public UserCreationResponse(String message, boolean success, String userId) {
        this.message = message;
        this.success = success;
        this.userId = userId;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
