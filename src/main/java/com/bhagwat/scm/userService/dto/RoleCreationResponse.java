package com.bhagwat.scm.userService.dto;

public class RoleCreationResponse {
    private String message;
    private boolean success;
    private String roleId;

    public RoleCreationResponse(String message, boolean success, String roleId) {
        this.message = message;
        this.success = success;
        this.roleId = roleId;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
}
