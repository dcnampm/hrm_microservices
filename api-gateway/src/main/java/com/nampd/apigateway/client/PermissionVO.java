package com.nampd.apigateway.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionVO implements Serializable {
    private Long id;
    private String description;
    private String name;

    public PermissionVO(String name) {
        this.name = name;
    }
}

