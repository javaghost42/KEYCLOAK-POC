package com.keycloak.poc.service;

import com.keycloak.poc.exception.ValidationException;
import com.keycloak.poc.request.UserCreateRequest;
import com.keycloak.poc.request.UserUpdateRequest;
import com.keycloak.poc.utils.ApplicationConstant;
import com.keycloak.poc.utils.ApplicationProperties;
import com.keycloak.poc.utils.UserUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private ApplicationProperties keycloakConfig;
    @Autowired
    private KeycloakInstanceBuilder keycloakInstanceBuilder;
    @Autowired
    private UserUtils userutils;

    public String createUser(UserCreateRequest request) {
        userutils.validateUsername(getAllUsers(), request.getUsername());
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setUsername(request.getUsername());
        userRepresentation.setEnabled(true);
        userRepresentation.singleAttribute("phoneNumber", request.getPhoneNumber());

        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(request.getPassword());
        userRepresentation.setCredentials(List.of(passwordCredentials));

        userRepresentation.setRequiredActions(List.of(ApplicationConstant.VERIFY_EMAIL));

        RoleRepresentation roleRepresentation;
        try {
            roleRepresentation = keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).roles().get(request.getRole()).toRepresentation();
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Invalid Role Provided");
        }
        String id = CreatedResponseUtil.getCreatedId(keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).users().create(userRepresentation));
        keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).users().get(id).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
        return "User Created Successfully";
    }

    public List<UserRepresentation> getAllUsers() {
        return keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).users().list();
    }

    public UserRepresentation getUserByUserName(String username) {
        try {
            return keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).users().search(username).get(0);
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "User Not Found.");
        }
    }

    public String updateUserByUserName(String userName, UserUpdateRequest updateRequest) {
        UserRepresentation user = getUserByUserName(userName);
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.singleAttribute("phoneNumber", updateRequest.getPhoneNumber());
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(updateRequest.getPassword());
        user.setCredentials(Collections.singletonList(credentialRepresentation));

        keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).users().get(user.getId()).update(user);
        RoleRepresentation roleRepresentation;
        try {
            roleRepresentation = keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).roles().get(updateRequest.getRole()).toRepresentation();
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Invalid Role Provided");
        }
        keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).users().get(user.getId()).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
        return "User Updated Successfully";
    }

    public String deleteUserByUserName(String userName) {
        UserRepresentation user = getUserByUserName(userName);
        keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).users().delete(user.getId());
        return "User Deleted Successfully";
    }

    public String getToken() {
        return keycloakInstanceBuilder.getInstance().tokenManager().getAccessTokenString();
    }
}
