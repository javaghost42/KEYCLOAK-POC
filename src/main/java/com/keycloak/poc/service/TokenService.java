package com.keycloak.poc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private KeycloakInstanceBuilder keycloakInstanceBuilder;

    public String getToken() {
        return "Bearer " + keycloakInstanceBuilder.getInstance().tokenManager().getAccessTokenString();
    }
}
