package com.bhagwat.scm.userService.service;

import com.bhagwat.scm.userService.dto.RoleCreationRequest;
import com.bhagwat.scm.userService.dto.RoleCreationResponse;
import com.bhagwat.scm.userService.entity.Role;
import com.bhagwat.scm.userService.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public RoleCreationResponse createRole(RoleCreationRequest request) {
        if (roleRepository.findByRoleName(request.getRoleName()).isPresent()) {
            return new RoleCreationResponse("Role with this name already exists.", false, null);
        }

        Role newRole = new Role();
        newRole.setRoleId(UUID.randomUUID().toString());
        newRole.setRoleName(request.getRoleName());
        newRole.setDescription(request.getDescription());
        newRole.setPermissions(request.getPermissions());

        Role savedRole = roleRepository.save(newRole);
        return new RoleCreationResponse("Role created successfully.", true, savedRole.getRoleId());
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
