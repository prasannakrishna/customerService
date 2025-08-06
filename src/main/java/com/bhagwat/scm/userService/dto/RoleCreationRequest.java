package com.bhagwat.scm.userService.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class RoleCreationRequest {
    @NotBlank
    private String roleName;
    private String description;
    private Map<String, Boolean> permissions;

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Boolean> getPermissions() { return permissions; }
    public void setPermissions(Map<String, Boolean> permissions) { this.permissions = permissions; }
}