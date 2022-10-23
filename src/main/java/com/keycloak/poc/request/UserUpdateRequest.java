package com.keycloak.poc.request;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String role;
}
