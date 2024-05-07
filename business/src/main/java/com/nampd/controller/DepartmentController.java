package com.nampd.controller;

import com.nampd.model.GenericResponse;
import com.nampd.model.dto.DepartmentDto;
import com.nampd.model.entity.Department;
import com.nampd.model.entity.Role;
import com.nampd.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Department>> createDepartment(@RequestBody Department department) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new GenericResponse<>(departmentService.createDepartment(department), 201, "Created department successfully", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>(null, 400, "Failed to create department: " + e.getMessage(), false));
        }
    }

    @GetMapping()
    public ResponseEntity<GenericResponse<List<DepartmentDto>>> getAllDepartments() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(departmentService.getAllDepartments()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<DepartmentDto>> getDepartmentByID(@PathVariable(name = "id") Long departmentId) {
        try {
            DepartmentDto departmentDto = departmentService.getDepartmentById(departmentId);
            return ResponseEntity.ok(new GenericResponse<>(departmentDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>(null, 400, "Failed to get department: " + e.getMessage(), false));
        }
    }

    @GetMapping("/search/{alias}")
    public ResponseEntity<GenericResponse<DepartmentDto>> getDepartmentByAlias(@PathVariable(name = "alias") String alias) {
        try {
            DepartmentDto departmentDto = departmentService.getDepartmentByAlias(alias);
            return ResponseEntity.ok(new GenericResponse<>(departmentDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>(null, 400, "Failed to get department: " + e.getMessage(), false));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<DepartmentDto>> updateDepartment(@PathVariable(name = "id") Long departmentId, @RequestBody Department department) {
        try {
            DepartmentDto departmentDto = departmentService.updateDepartment(departmentId, department);
            return ResponseEntity.ok(new GenericResponse<>(departmentDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>(null, 400, "Failed to update department: " + e.getMessage(), false));
        }
    }

    @Operation(
            summary = "Delete department and every role in this department"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Object>> deleteDepartment(@PathVariable(name = "id") Long departmentId) {
        try {
            departmentService.deleteDepartment(departmentId);
            return new ResponseEntity<>(new GenericResponse<>(null, 200), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new GenericResponse<>(null, 500, "Failed to delete department: " + e.getMessage(), false));
        }
    }

    @Operation(
            summary = "Retrieve roles in department",
            description = "Get all roles in a department by specifying department's id. The response is a list of roles with id and name"
    )
    @GetMapping("/roles/{departmentId}")
    public ResponseEntity<GenericResponse<List<Role>>> getRolesInDepartmentById(@PathVariable(name = "departmentId") Long departmentId) {
        List<Role> roles = departmentService.getRolesInDepartment(departmentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(roles));
    }

}
