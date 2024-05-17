package com.nampd.authservice.controller;


import com.nampd.authservice.GenericResponse;
import com.nampd.authservice.model.entity.Permission;
import com.nampd.authservice.model.entity.Role;
import com.nampd.authservice.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("api/v1/auth/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/in-department/{departmentId}")
    public List<Role> getRolesInDepartmentById(@PathVariable(name = "departmentId") Long departmentId) {
        try {
            return roleService.getRolesInDepartment(departmentId);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get roles in department: " + e.getMessage());
        }
    }

    @GetMapping("/{roleName}")
    public Role getRoleByName(@PathVariable(name = "roleName") String role) {
        try {
            return roleService.getRoleByName(role);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get role: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Role>> createRole(@RequestBody Role role, @RequestParam Long departmentId) {
        try {
            Role createdRole = roleService.createRole(role, departmentId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new GenericResponse<>(createdRole, 201, "Created role successfully", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>(null, 400, "Failed to create role: " + e.getMessage(), false));
        }
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<GenericResponse<Role>> updateRole (@PathVariable Long roleId,
                                                             @RequestBody Role updatedRole,
                                                             @RequestParam Set<Long> permissionIds) {
        try {
            Role role = roleService.updateRole(roleId, updatedRole, permissionIds);
            return ResponseEntity.ok(new GenericResponse<>(role));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<>(null, HttpStatus.NOT_FOUND.value(), e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update role", false));
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<GenericResponse<String>> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return new ResponseEntity<>(new GenericResponse<>(null, 200), HttpStatus.OK);
    }
}
