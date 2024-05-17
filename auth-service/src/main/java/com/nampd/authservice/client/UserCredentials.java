package com.nampd.authservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCredentials implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String department;
}
