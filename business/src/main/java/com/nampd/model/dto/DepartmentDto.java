package com.nampd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class DepartmentDto implements Serializable {
    private final String name;
    private final String alias;
    private final Set<UserDto> users;

}
