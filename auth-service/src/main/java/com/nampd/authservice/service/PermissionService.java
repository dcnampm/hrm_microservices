package com.nampd.authservice.service;

import com.nampd.authservice.model.entity.Permission;

import java.util.Set;

public interface PermissionService {
    Permission savePermission(Permission permission);

    void createPermissions();

    Set<Permission> getPermissionsByRoleId(Long roleId);
}
