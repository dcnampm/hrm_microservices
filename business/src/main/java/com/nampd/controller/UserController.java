package com.nampd.controller;

import com.nampd.model.GenericResponse;
import com.nampd.model.dto.UserCredentialsDto;
import com.nampd.model.dto.UserDto;
import com.nampd.model.entity.User;
import com.nampd.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/business/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<User>> addUser(@RequestBody User user) {
        try {
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(new GenericResponse<>(userService.addUser(user), 201, "Created user successfully", true));
            return new ResponseEntity<>(new GenericResponse<>(userService.addUser(user)), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>(null, 400, "Failed to create user: " + e.getMessage(), false));
        }
    }

    @GetMapping()
    public ResponseEntity<GenericResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(userService.getAllUsers()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GenericResponse<UserDto>> getUserById(@PathVariable(name = "userId") Long userId) {
        try {
            UserDto userDto = userService.getUserById(userId);
            return ResponseEntity.ok(new GenericResponse<>(userDto));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>(null, 400, "Failed to get user: " + e.getMessage(), false));
        }
    }

    @GetMapping("/auth-service/{email}")
    public UserCredentialsDto getUserByEmail(@PathVariable(name = "email") String email) {
        try {
            return userService.getUserByEmail(email);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get user: " + e.getMessage());
        }
    }


    @GetMapping("/search/{firstName}")
    public ResponseEntity<GenericResponse<List<UserDto>>> getUserByFirstName(@PathVariable String firstName) {
        try {
            List<UserDto> userDtos = userService.getUserByFirstName(firstName);
            return ResponseEntity.ok(new GenericResponse<>(userDtos));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>(null, 400, "Failed to get users: " + e.getMessage(), false));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<User>> updateUser(@PathVariable(name = "id") Long userId, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(userId, updatedUser);
            return ResponseEntity.ok(new GenericResponse<>(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>(null, 400, "Failed to update user: " + e.getMessage(), false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Object>> deleteUser(@PathVariable(name = "id") Long userId) {
        try {
            userService.deleteUser(userId);
            return new ResponseEntity<>(new GenericResponse<>(null, 200), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new GenericResponse<>(null, 500, "Failed to delete user: " + e.getMessage(), false));
        }
    }

    @Operation(
            summary = "Add user to a department and assign role to user"
    )
    @PutMapping("/assignRole/{username}")
    public ResponseEntity<String> assignRoleToUser(@PathVariable String username,
                                                   @RequestParam String role,
                                                   @RequestParam Long departmentId) {
        userService.assignRoleAndDepartmentToUser(username, role, departmentId);
        return ResponseEntity.ok("Assigning user to department and assigning role to user " + username + " successfully");
    }

    @Operation(
            summary = "Unassign user's role and remove user from department"
    )
    @PutMapping("/remove/{username}")
    public ResponseEntity<String> removeUserFromDepartmentAndUnassginRole(@PathVariable String username) {
        userService.removeUserFromDepartmentAndUnassignRole(username);
        return ResponseEntity.ok("Remove user from department successfully");
    }
}
