package com.keycloak.poc.controller;

import com.keycloak.poc.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public String getToken() {
        return tokenService.getToken();
    }
}
