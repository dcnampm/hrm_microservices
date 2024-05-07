package com.nampd.service.impl;

import com.nampd.model.entity.Department;
import com.nampd.model.entity.Permission;
import com.nampd.model.entity.Role;
import com.nampd.repository.DepartmentRepository;
import com.nampd.repository.PermissionRepository;
import com.nampd.repository.RoleRepository;
import com.nampd.service.RoleService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;


@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, DepartmentRepository departmentRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Role createRole(Role role, Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department with id " + departmentId + " not found"));

        Role existingRole = roleRepository.findByNameAndDepartmentId(role.getName(), departmentId);
        if (existingRole != null) {
            throw new IllegalArgumentException("Role '" + role.getName() + "' already exists in department with id " + departmentId);
        }

        role.setDepartment(department);
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long roleId, Role updatedRole, Set<Long> permissionIds) {
        Role existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role not found"));

        existingRole.setName(updatedRole.getName());
        existingRole.setDescription(updatedRole.getDescription());

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        existingRole.setPermissions(permissions);

        return roleRepository.save(existingRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Role roleToBeDeleted = roleRepository.findById(roleId).orElseThrow(() -> new NoSuchElementException("Role not found"));
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role getRoleByName(String roleName) {
        Role foundRole = roleRepository.findByName(roleName);

        if (foundRole != null) {
            return foundRole;
        } else {
            throw new NoSuchElementException("Not found roles with first name: " + roleName);
        }
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
