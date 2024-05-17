package com.nampd.client;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleVO implements Serializable {
    private Long id;

    private String name;

    private String description;

    private String department;

}
