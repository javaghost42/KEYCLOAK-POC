package com.keycloak.poc.controller;

import com.keycloak.poc.request.CompositeRoleRequest;
import com.keycloak.poc.request.RoleCreateRequest;
import com.keycloak.poc.response.RoleDetails;
import com.keycloak.poc.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public String createRole(@RequestBody RoleCreateRequest request) {
        return roleService.createRole(request);
    }

    @PostMapping("/compositeRole")
    public String createCompositeRole(@RequestBody CompositeRoleRequest compositeRoleRequest){
       return roleService.createCompositeRole(compositeRoleRequest);
    }

    @GetMapping
    public List<RoleDetails> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{roleName}")
    public RoleDetails getRoleDetailsByRoleName(@PathVariable String roleName) {
        return roleService.getRoleByRoleName(roleName);
    }

    @GetMapping("/user/{userName}")
    public List<RoleDetails> getUserRoles(@PathVariable String userName) {
        return roleService.getUserRoles(userName);
    }

    @PutMapping("/{roleName}")
    public String updateRoleByRoleName(@PathVariable String roleName, @RequestBody RoleCreateRequest request) {
        return roleService.updateRoleByRoleName(roleName, request);
    }

    @DeleteMapping("/{roleName}")
    public String deleteRoleByRoleName(@PathVariable String roleName) {
        return roleService.deleteRoleByRoleName(roleName);
    }

}
