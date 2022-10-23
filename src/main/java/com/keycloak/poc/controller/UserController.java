package com.keycloak.poc.controller;

import com.keycloak.poc.request.UserCreateRequest;
import com.keycloak.poc.request.UserUpdateRequest;
import com.keycloak.poc.service.UserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public String saveUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserRepresentation> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userName}")
    public UserRepresentation getUserByUserName(@PathVariable String userName) {
        return userService.getUserByUserName(userName);
    }

    @PutMapping("/{userName}")
    public String updateUserByUserName(@PathVariable String userName, @RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateUserByUserName(userName, userUpdateRequest);
    }

    @DeleteMapping("/{userName}")
    public String deleteUserByUserName(@PathVariable String userName) {
        return userService.deleteUserByUserName(userName);
    }

    @GetMapping("/token")
    public String getToken() {
        return userService.getToken();
    }
}
