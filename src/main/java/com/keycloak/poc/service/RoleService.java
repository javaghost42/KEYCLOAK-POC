package com.keycloak.poc.service;

import com.keycloak.poc.exception.ValidationException;
import com.keycloak.poc.request.CompositeRoleRequest;
import com.keycloak.poc.request.RoleCreateRequest;
import com.keycloak.poc.response.RoleDetails;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.*;
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
        try {
            keycloakInstanceBuilder.getInstanceWithRealm().roles().create(roleRepresentation);
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Unable to create role ,please try again after some time");
        }
        return "Role Created Successfully";
    }

    public List<RoleDetails> getAllRoles() {
        return keycloakInstanceBuilder.getInstanceWithRealm().roles().list().stream().map(role -> new RoleDetails(role.getName(), role.getDescription())).collect(Collectors.toList());
    }

    public RoleDetails getRoleByRoleName(String roleName) {
        try {
            RoleRepresentation role = keycloakInstanceBuilder.getInstanceWithRealm().roles().get(roleName).toRepresentation();
            return new RoleDetails(role.getName(), role.getDescription());
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Role Not Found");
        }
    }

    public String updateRoleByRoleName(String roleName, RoleCreateRequest request) {
        try {
            RoleRepresentation roleRepresentation = keycloakInstanceBuilder.getInstanceWithRealm().roles().get(roleName).toRepresentation();
            roleRepresentation.setName(request.getRoleName());
            roleRepresentation.setDescription(request.getRoleDescription());
            keycloakInstanceBuilder.getInstanceWithRealm().roles().get(roleName).update(roleRepresentation);
        } catch (NotFoundException exception) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Role Not Found");
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Unable to update role ,please try again after some time");
        }
        return "Role Updated Successfully";
    }

    public String deleteRoleByRoleName(String roleName) {
        getRoleByRoleName(roleName);
        try {
            keycloakInstanceBuilder.getInstanceWithRealm().roles().deleteRole(roleName);
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Unable to delete role ,please try again after some time");
        }
        return "Role Deleted Successfully";
    }

    public List<RoleDetails> getUserRoles(String userName) {
        try {
            return keycloakInstanceBuilder.getInstanceWithRealm().users().get(userService.getUserByUserName(userName).getId()).roles().realmLevel().listAll().stream().map(role -> new RoleDetails(role.getName(), role.getDescription())).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "User Not Found");
        }
    }

    public void assignRoleToUser(String role, String id) {
        RoleRepresentation roleRepresentation;
        try {
            roleRepresentation = keycloakInstanceBuilder.getInstanceWithRealm().roles().get(role).toRepresentation();
            keycloakInstanceBuilder.getInstanceWithRealm().users().get(id).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
        } catch (NotFoundException exception) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Invalid Role Provided");
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Unable to assign role to user ,please try again after some time");
        }
    }

    public String createCompositeRole(CompositeRoleRequest compositeRoleRequest) {
        try {
            List<RoleRepresentation> clientRolesList = new ArrayList<>();
            List<RoleRepresentation> compositeRolesList = new ArrayList<>();

            keycloakInstanceBuilder.getInstanceWithRealm().roles().get(compositeRoleRequest.getRoleName()).deleteComposites(new ArrayList<>(keycloakInstanceBuilder.getInstanceWithRealm().roles().get(compositeRoleRequest.getRoleName()).getRealmRoleComposites()));
            if (!compositeRoleRequest.getClientRoles().isEmpty()) compositeRoleRequest.getClientRoles().forEach((clientName, clientCompositeRoles) -> keycloakInstanceBuilder.getInstanceWithRealm().clients().get(getClientId(clientName, keycloakInstanceBuilder.getInstanceWithRealm())).roles().list().forEach(role -> clientCompositeRoles.stream().filter(ccr -> role.getName().equalsIgnoreCase(ccr)).map(ele -> clientRolesList.add(role)).toList()));

            compositeRolesList.addAll(validateAndGetAllRealmRoles(compositeRoleRequest.getRealmRoles(), keycloakInstanceBuilder.getInstanceWithRealm().roles()));
            compositeRolesList.addAll(clientRolesList);
            keycloakInstanceBuilder.getInstanceWithRealm().roles().get(compositeRoleRequest.getRoleName()).addComposites(compositeRolesList);
        } catch (NotFoundException exception) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Role Not Found.");
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Unable to create composite role ,please try again some time.");
        }
        return "Composite Role Created Successfully";
    }

    public String getClientId(String clientName, RealmResource realmResource) {
        try {
            return realmResource.clients().findAll().stream().filter(client -> client.getClientId().equalsIgnoreCase(clientName)).toList().get(0).getId();
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Invalid Client Name Provided : " + clientName);
        }
    }

    public List<RoleRepresentation> validateAndGetAllRealmRoles(List<String> realmRoles, RolesResource rolesResource) {
        List<RoleRepresentation> compositeRolesList = new ArrayList<>();
        try {
            if (!realmRoles.isEmpty()) {
                realmRoles.forEach(role -> compositeRolesList.add(rolesResource.get(role).toRepresentation()));
            }
            return compositeRolesList;
        } catch (NotFoundException e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Please Provide Valid Realm Role.");
        }
    }
}