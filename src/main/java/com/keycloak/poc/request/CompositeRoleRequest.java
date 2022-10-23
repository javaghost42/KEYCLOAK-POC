package com.keycloak.poc.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompositeRoleRequest {

    String roleName;
    List<String> realmRoles;
    Map<String, List<String>> clientRoles;
}
