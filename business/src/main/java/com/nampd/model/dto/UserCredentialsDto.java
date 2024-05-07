package com.nampd.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserCredentialsDto implements Serializable {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final String role;
    private final String department;
}
