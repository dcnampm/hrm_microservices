package com.nampd.authservice.service;


import com.nampd.authservice.model.entity.Permission;
import com.nampd.authservice.model.entity.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<Role> getRolesInDepartment(Long departmentId);
    Role createRole(Role role, Long departmentId);
    void deleteRole(Long roleId);
    Role getRoleByName(String role);
    Role updateRole(Long roleId, Role updatedRole, Set<Long> permissionIds);
    Set<Permission> getPermissionsByRoleId(Long roleId);
    Set<Permission> getPermissionsForRole(String role);
}
