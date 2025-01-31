package com.mindhub.user_microservice.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    private RolType roles = RolType.USER;
    private UserStatus userStatus = UserStatus.PENDING;


    public UserEntity() {
    }

    public UserEntity(String username, String email, RolType roles, String password) {
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.password = password;
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
