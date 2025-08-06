package com.bhagwat.scm.userService.entity;
import com.bhagwat.scm.userService.constants.RoleType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "role_id", unique = true, nullable = false, length = 50)
    private String roleId;

    @Column(name = "role_name", unique = true, nullable = false, length = 100)
    private String roleName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "permissions", columnDefinition = "jsonb")
    private Map<String, Boolean> permissions = new HashMap<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @Column(name = "role_type")
    private RoleType roleType;

    public Role() {}

    public Role(String roleId, String roleName, String description, Map<String, Boolean> permissions) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
        this.permissions = permissions != null ? permissions : new HashMap<>();
    }

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Boolean> getPermissions() { return permissions; }
    public void setPermissions(Map<String, Boolean> permissions) { this.permissions = permissions; }
    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) {
        this.users.clear();
        if (users != null) this.users.addAll(users);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId='" + roleId + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}