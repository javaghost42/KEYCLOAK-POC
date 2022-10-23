package com.keycloak.poc.request;

import lombok.Data;

@Data
public class UserCreateRequest {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private String role;
}
