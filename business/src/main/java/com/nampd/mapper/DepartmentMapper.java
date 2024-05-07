package com.nampd.mapper;

import com.nampd.model.dto.DepartmentDto;
import com.nampd.model.dto.UserDto;
import com.nampd.model.entity.Department;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DepartmentMapper {
    private final UserMapper userMapper;

    public DepartmentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Department toDepartment(DepartmentDto dto) {
        var department = new Department();
        department.setName(dto.getName());
        department.setAlias(dto.getAlias());

        return department;
    }

    public DepartmentDto toDepartmentDto(Department department) {
        Set<UserDto> userDtos = department.getUsers()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toSet());

        return new DepartmentDto(
                department.getName(),
                department.getAlias(),
                userDtos
        );
    }

}
