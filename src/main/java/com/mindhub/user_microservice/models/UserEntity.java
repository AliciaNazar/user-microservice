package com.mindhub.user_microservice.models;

import jakarta.persistence.*;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String email;
    private RolType roles = RolType.USER;

    public UserEntity() {
    }

    public UserEntity(String username, String email, RolType roles) {
        this.username = username;
        this.email = email;
        this.roles = roles;
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
}
