package com.keycloak.poc.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleCreateRequest {

    private String roleName;
    private String roleDescription;
}
