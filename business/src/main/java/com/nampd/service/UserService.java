package com.nampd.service;

import com.nampd.model.dto.UserCredentialsDto;
import com.nampd.model.dto.UserDto;
import com.nampd.model.entity.User;
//import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserService {
//    @PreAuthorize("hasAnyAuthority(\"USER.READ\") or hasAnyRole('ROLE_TPNS')")
    UserDto getUserById(Long id);

    UserCredentialsDto getUserByEmail(String email);

    //    @PreAuthorize("hasAnyAuthority(\"USER.READ\") or hasAnyRole('ROLE_TPNS')")
    List<UserDto> getUserByFirstName(String firstName);
//    @PreAuthorize("hasAnyAuthority(\"USER.UPDATE\") or hasAnyRole('ROLE_TPNS')")
    User updateUser(Long id, User updatedUser);
//    @PreAuthorize("hasAnyAuthority(\"USER.DELETE\") or hasAnyRole('ROLE_TPNS')")
    void deleteUser(Long id);
//    @PreAuthorize("hasAnyAuthority(\"USER.CREATE\") or hasAnyRole('ROLE_TPNS')")
    User addUser(User user);
//    @PreAuthorize("hasAnyAuthority(\"USER.READ\") or hasAnyRole('ROLE_TPNS')")
    List<UserDto> getAllUsers();
//    @PreAuthorize("hasAnyAuthority(\"USER.UPDATE\") or hasAnyRole('ROLE_TPNS')")
    void assignRoleAndDepartmentToUser(String username, String roleName, Long departmentId);
    //remove user from department
//    @PreAuthorize("hasAnyAuthority(\"USER.UPDATE\") or hasAnyRole('ROLE_TPNS')")
    void removeUserFromDepartmentAndUnassignRole(String username);
}
