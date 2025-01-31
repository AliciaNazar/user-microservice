package com.mindhub.user_microservice.dtos;

import com.mindhub.user_microservice.models.RolType;
import com.mindhub.user_microservice.models.UserStatus;

public class UserRegistrationDTO {
    private Long id;
    private String username;
    private String email;
    private RolType role;
    private UserStatus userStatus;

    public UserRegistrationDTO(Long id, String username, String email, RolType role, UserStatus userStatus) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.userStatus = userStatus;
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

    public RolType getRole() {
        return role;
    }

    public void setRole(RolType role) {
        this.role = role;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}


