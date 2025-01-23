package com.mindhub.user_microservice.dtos;

import com.mindhub.user_microservice.models.RolType;
import com.mindhub.user_microservice.models.UserEntity;

public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private RolType roles;

    public UserDTO() {
    }

    public UserDTO(UserEntity user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
