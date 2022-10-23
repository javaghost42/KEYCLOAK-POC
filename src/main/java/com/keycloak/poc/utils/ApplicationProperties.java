package com.keycloak.poc.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ApplicationProperties {

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.url}")
    private String url;
    @Value("${keycloak.clientName}")
    private String clientId;
    @Value("${keycloak.clientSecret}")
    private String clientSecret;

}
