package com.nampd.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PermissionDto implements Serializable {
    private final Long id;
    private final String name;
    private final String description;
}
