package com.nampd.authservice.service.impl;

import com.nampd.authservice.model.entity.Permission;
import com.nampd.authservice.model.entity.Role;
import com.nampd.authservice.repository.PermissionRepository;
import com.nampd.authservice.repository.RoleRepository;
import com.nampd.authservice.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Permission savePermission(Permission permission) {
        if (permissionRepository.existsByName(permission.getName())) {
            throw new IllegalArgumentException("Permission already exists");
        }
        return permissionRepository.save(permission);
    }

    @Override
    public void createPermissions() {
        Set<String> permissionNames = Set.of(
                "DEPARTMENT.READ",
                "DEPARTMENT.CREATE",
                "DEPARTMENT.UPDATE",
                "DEPARTMENT.DELETE",
                "USER.READ",
                "USER.CREATE",
                "USER.UPDATE",
                "USER.DELETE",
                "ROLE.READ",
                "ROLE.CREATE",
                "ROLE.DELETE"
        );
        for (String permissionName : permissionNames) {
            Permission permission = new Permission();
            permission.setName(permissionName);
            try {
                permissionRepository.save(permission);
            } catch (Exception e) {
                System.out.println("Permission " + permissionName + " already exists");
            }
        }
    }

    @Override
    public Set<Permission> getPermissionsByRoleId(Long roleId) {
        Role foundRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role not found"));

        return foundRole.getPermissions();
    }
}
