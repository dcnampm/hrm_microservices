package com.nampd.mapper;

import com.nampd.model.dto.UserCredentialsDto;
import com.nampd.model.dto.UserDto;
import com.nampd.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getPhone(),
                user.getAddress(),
                user.getBirthday(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getDepartment() != null ? user.getDepartment().getName() : null
        );
    }

    public UserCredentialsDto toUserCredentialsDto(User user) {
        return new UserCredentialsDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getDepartment() != null ? user.getDepartment().getName() : null
        );
    }
}
