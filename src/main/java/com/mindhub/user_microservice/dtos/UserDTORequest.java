package com.mindhub.user_microservice.dtos;

import com.mindhub.user_microservice.models.RolType;
import com.mindhub.user_microservice.models.UserEntity;

public class UserDTORequest {

    private String username;
    private String email;
    private RolType roles;

    public UserDTORequest() {
    }

    public UserDTORequest(UserEntity user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RolType getRoles() {
        return roles;
    }

    public void setRoles(RolType roles) {
        this.roles = roles;
    }
}
