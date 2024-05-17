package com.nampd.authservice.service.impl;


import com.nampd.authservice.client.DepartmentVO;
import com.nampd.authservice.model.entity.Permission;
import com.nampd.authservice.model.entity.Role;
import com.nampd.authservice.repository.PermissionRepository;
import com.nampd.authservice.repository.RoleRepository;
import com.nampd.authservice.service.RoleService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RestTemplate restTemplate;
    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository, RestTemplate restTemplate) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Role> getRolesInDepartment(Long departmentId) {
        DepartmentVO departmentVO = restTemplate.getForObject("/departments/{id}", DepartmentVO.class, departmentId);

        if (departmentVO != null) {
            String departmentName = departmentVO.getName();
            return roleRepository.findByDepartment(departmentName);
        } else {
            throw new NoSuchElementException("Department not found");
        }
    }

    @Override
    public Role createRole(Role role, Long departmentId) {
        DepartmentVO departmentVO = restTemplate.getForObject("/departments/{id}", DepartmentVO.class, departmentId);

        if (departmentVO != null) {
            Role existingRole = roleRepository.findByNameAndDepartment(role.getName(), departmentVO.getName());
            if (existingRole != null) {
                throw new IllegalArgumentException("Role '" + role.getName() + "' already exists in department: " + departmentVO.getName());
            }

            role.setDepartment(departmentVO.getName());
            return roleRepository.save(role);

        } else {
            throw new NoSuchElementException("Department not found");
        }
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
    public Role getRoleByName(String role) {
        Role foundRole = roleRepository.findByName(role);

        if (foundRole != null) {
            return foundRole;
        } else {
            throw new NoSuchElementException("Not found roles: " + role);
        }
    }

    @Override
    public Set<Permission> getPermissionsByRoleId(Long roleId) {
        Role foundRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role not found"));

        return foundRole.getPermissions();
    }

    @Override
    public Set<Permission> getPermissionsForRole(String role) {
        Role foundRole = roleRepository.findByName(role);

        if (foundRole == null) {
            throw new NoSuchElementException("Role not found");
        }

        return foundRole.getPermissions();
    }
}
