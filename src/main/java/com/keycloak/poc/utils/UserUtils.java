package com.keycloak.poc.utils;

import com.keycloak.poc.exception.ValidationException;
import com.keycloak.poc.request.UserCreateRequest;
import com.keycloak.poc.request.UserUpdateRequest;
import com.keycloak.poc.service.KeycloakInstanceBuilder;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserUtils {

    @Autowired
    private KeycloakInstanceBuilder keycloakInstanceBuilder;

    public void validateUsername(String userName) {
        var usersList = keycloakInstanceBuilder.getInstanceWithRealm().users().list();
        if (!usersList.isEmpty()) {
            usersList.forEach(ele -> {
                if (ele.getUsername().equalsIgnoreCase(userName)) {
                    throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "User already exists with this username.");
                }
            });
        }
    }

    public String saveUser(UserCreateRequest request) {
        validateUsername(request.getUsername());
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

        try {
            return CreatedResponseUtil.getCreatedId(keycloakInstanceBuilder.getInstanceWithRealm().users().create(userRepresentation));
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Unable to create user ,please try again after some time");
        }
    }

    public void updateUser(UserUpdateRequest updateRequest, UserRepresentation user) {
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.singleAttribute("phoneNumber", updateRequest.getPhoneNumber());
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(updateRequest.getPassword());
        user.setCredentials(Collections.singletonList(credentialRepresentation));

        try {
            keycloakInstanceBuilder.getInstanceWithRealm().users().get(user.getId()).update(user);
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Unable to update user ,please try again after some time");
        }
    }

    public UserRepresentation loadUserByUserName(String userName) {
        try {
            return keycloakInstanceBuilder.getInstanceWithRealm().users().search(userName).get(0);
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "User Not Found.");
        }
    }
}