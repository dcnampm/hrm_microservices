package com.nampd.authservice.controller;

import com.nampd.authservice.GenericResponse;
import com.nampd.authservice.model.entity.Permission;
import com.nampd.authservice.service.PermissionService;
import com.nampd.authservice.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("api/v1/auth/permissions")
public class PermissionController {
    private final PermissionService permissionService;
    private final RoleService roleService;

    public PermissionController(PermissionService permissionService, RoleService roleService) {
        this.permissionService = permissionService;
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Permission>> createPermission(@RequestBody Permission permission) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new GenericResponse<>(permissionService.savePermission(permission), 201, "Created permission successfully", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>(null, 400, "Failed to create permission: " + e.getMessage(), false));
        }
    }

    @PostMapping("/add-all")
    public void createPermissions() {
        permissionService.createPermissions();
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<GenericResponse<Set<Permission>>> getAllPermissionsByRoleId(@PathVariable Long roleId) {
        try {
            Set<Permission> permissions = roleService.getPermissionsByRoleId(roleId);
            return ResponseEntity.ok(new GenericResponse<>(permissions));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<>(null, HttpStatus.NOT_FOUND.value(), e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to get permisisons", false));
        }
    }

    @GetMapping("/for-role/{role}")
    public Set<Permission> getPermissionsForRole(@PathVariable String role) {
        try {
            return roleService.getPermissionsForRole(role);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }
}
