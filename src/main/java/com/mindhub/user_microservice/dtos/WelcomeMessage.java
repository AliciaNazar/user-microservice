package com.mindhub.user_microservice.dtos;

public record WelcomeMessage(
        String username,
        String email,
        String token
) {
}
