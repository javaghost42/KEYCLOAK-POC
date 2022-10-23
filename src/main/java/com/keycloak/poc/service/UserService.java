package com.keycloak.poc.service;

import com.keycloak.poc.exception.ValidationException;
import com.keycloak.poc.request.UserCreateRequest;
import com.keycloak.poc.request.UserUpdateRequest;
import com.keycloak.poc.response.UserDetails;
import com.keycloak.poc.utils.ApplicationProperties;
import com.keycloak.poc.utils.UserUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.BeanUtils;
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
    private RoleService roleService;
    @Autowired
    private UserUtils userutils;

    public String createUser(UserCreateRequest request) {
        String id = userutils.saveUser(request);
        roleService.assignRoleToUser(request.getRole(), id);
        return "User Created Successfully";
    }

    public List<UserDetails> getAllUsers() {
        List<UserRepresentation> users = keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).users().list();
        List<UserDetails> userDetailsList = new ArrayList<>();
        users.forEach(user -> {
            UserDetails userDetails = new UserDetails();
            BeanUtils.copyProperties(user, userDetails);
            userDetails.setRoles(roleService.getUserRoles(user.getUsername()));
            userDetailsList.add(userDetails);
        });
        return userDetailsList;
    }

    public UserDetails getUserByUserName(String username) {
        try {
            UserRepresentation user = keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).users().search(username).get(0);
            UserDetails userDetails = new UserDetails();
            BeanUtils.copyProperties(user, userDetails);
            userDetails.setRoles(roleService.getUserRoles(user.getUsername()));
            return userDetails;
        } catch (Exception e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "User Not Found.");
        }
    }

    public String updateUserByUserName(String userName, UserUpdateRequest updateRequest) {
        UserRepresentation user = userutils.loadUserByUserName(userName);
        userutils.updateUser(updateRequest, user);
        roleService.assignRoleToUser(updateRequest.getRole(), user.getId());
        return "User Updated Successfully";
    }

    public String deleteUserByUserName(String userName) {
        UserDetails user = getUserByUserName(userName);
        keycloakInstanceBuilder.getInstance().realm(keycloakConfig.getRealm()).users().delete(user.getId());
        return "User Deleted Successfully";
    }
}