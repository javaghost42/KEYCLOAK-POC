package com.keycloak.poc.service;

import com.keycloak.poc.utils.ApplicationProperties;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.keycloak.poc.utils.ApplicationConstant.CLIENT_KEYS;

@Component
public class KeycloakInstanceBuilder {

    private static final ConcurrentHashMap<String, Keycloak> keycloakClients = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationProperties keycloakConfigs;

    public Keycloak keycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakConfigs.getUrl())
                .realm(keycloakConfigs.getRealm()).grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(keycloakConfigs.getClientId())
                .clientSecret(keycloakConfigs.getClientSecret())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
    }

    public Keycloak getInstance() {
        Keycloak keycloak = keycloakClients.get(CLIENT_KEYS);
        if (Objects.isNull(keycloak)) {
            keycloak = keycloakClient();
            keycloakClients.put(CLIENT_KEYS, keycloak);
        }
        return keycloak;
    }

    public RealmResource getInstanceWithRealm() {
        return getInstance().realm(keycloakConfigs.getRealm());
    }

}
