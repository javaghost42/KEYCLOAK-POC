package com.keycloak.poc.service;

import com.keycloak.poc.utils.ApplicationProperties;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeycloakInstanceBuilder {

    @Autowired
    private ApplicationProperties keycloakConfigs;

    public Keycloak getInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakConfigs.getUrl())
                .realm(keycloakConfigs.getRealm()).grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(keycloakConfigs.getClientId())
                .clientSecret(keycloakConfigs.getClientSecret())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
    }

    public RealmResource getInstanceWithRealm() {
        return getInstance().realm(keycloakConfigs.getRealm());
    }

}
