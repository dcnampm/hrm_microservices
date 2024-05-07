package com.nampd.controller;

import com.nampd.model.GenericResponse;
import com.nampd.model.entity.Permission;
import com.nampd.model.entity.Role;
import com.nampd.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(
            summary = "Create role in a department(specified by id)"
    )
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
    public ResponseEntity<GenericResponse<Role>> updateRole (@PathVariable(name = "roleId") Long roleId,
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
    public ResponseEntity<GenericResponse<String>> deleteRole(@PathVariable(name = "roleId") Long roleId) {
        roleService.deleteRole(roleId);
        return new ResponseEntity<>(new GenericResponse<>(null, 200), HttpStatus.OK);
    }

    @PostMapping("/permissions")
    public void createPermissions() {
        roleService.createPermissions();
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<GenericResponse<Set<Permission>>> getAllPermissionsByRoleId(@PathVariable(name = "roleId") Long roleId) {
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
}
