package com.keycloak.poc.utils;

import com.keycloak.poc.exception.ValidationException;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserUtils {

    public void validateUsername(List<UserRepresentation> usersList, String userName) {
        if (!usersList.isEmpty()) {
            usersList.forEach(ele -> {
                if (ele.getUsername().equalsIgnoreCase(userName)) {
                    throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "User already exists with this username.");
                }
            });
        }
    }
}
