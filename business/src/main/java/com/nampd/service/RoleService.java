package com.nampd.service;

import com.nampd.model.entity.Permission;
import com.nampd.model.entity.Role;
//import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

public interface RoleService {
//    @PreAuthorize("hasAnyAuthority(\"ROLE.CREATE\") or hasAnyRole('ROLE_TPNS')")
    Role createRole(Role role, Long departmentId);
//    @PreAuthorize("hasAnyAuthority(\"ROLE.DELETE\") or hasAnyRole('ROLE_TPNS')")
    void deleteRole(Long roleId);
//    @PreAuthorize("hasAnyAuthority(\"ROLE.READ\") or hasAnyRole('ROLE_TPNS')")
    Role getRoleByName(String roleName);
    void createPermissions();
//    @PreAuthorize("hasAnyAuthority(\"ROLE.UPDATE\") or hasAnyRole('ROLE_TPNS')")
    Role updateRole(Long roleId, Role updatedRole, Set<Long> permissionIds);
//    @PreAuthorize("hasAnyAuthority(\"ROLE.READ\") or hasAnyRole('ROLE_TPNS')")
    Set<Permission> getPermissionsByRoleId(Long roleId);
}
