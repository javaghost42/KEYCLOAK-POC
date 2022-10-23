package com.keycloak.poc.service;

import com.keycloak.poc.exception.ValidationException;
import com.keycloak.poc.request.RoleCreateRequest;
import com.keycloak.poc.response.RoleDetails;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private KeycloakInstanceBuilder keycloakInstanceBuilder;
    @Autowired
    private UserService userService;

    public String createRole(RoleCreateRequest request) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(request.getRoleName());
        roleRepresentation.setDescription(request.getRoleDescription());
        keycloakInstanceBuilder.getInstanceWithRealm().roles().create(roleRepresentation);
        return "Role Created Successfully";
    }

    public List<RoleDetails> getAllRoles() {
        return keycloakInstanceBuilder.getInstanceWithRealm().roles().list().stream().map(role -> new RoleDetails(role.getName(), role.getDescription())).collect(Collectors.toList());
    }

    public RoleDetails getRoleDetailsByRoleName(String roleName) {
        try {
            RoleRepresentation role = keycloakInstanceBuilder.getInstanceWithRealm().roles().get(roleName).toRepresentation();
            return new RoleDetails(role.getName(), role.getDescription());
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Role Not Found");
        }
    }

    public String updateRoleByRoleName(String roleName, RoleCreateRequest request) {
        RoleRepresentation roleRepresentation = keycloakInstanceBuilder.getInstanceWithRealm().roles().get(roleName).toRepresentation();
        roleRepresentation.setName(request.getRoleName());
        roleRepresentation.setDescription(request.getRoleDescription());
        keycloakInstanceBuilder.getInstanceWithRealm().roles().get(roleName).update(roleRepresentation);
        return "Role Updated Successfully";
    }

    public String deleteRoleByRoleName(String roleName) {
        getRoleDetailsByRoleName(roleName);
        keycloakInstanceBuilder.getInstanceWithRealm().roles().deleteRole(roleName);
        return "Role Deleted Successfully";
    }

    public List<RoleDetails> getUserRoles(String userName) {
        try {
            return keycloakInstanceBuilder.getInstanceWithRealm().users().get(userService.getUserByUserName(userName).getId()).roles().realmLevel().listAll().stream().map(role -> new RoleDetails(role.getName(), role.getDescription())).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "User Not Found");
        }
    }
}
