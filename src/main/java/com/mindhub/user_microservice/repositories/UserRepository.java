package com.mindhub.user_microservice.repositories;

import com.mindhub.user_microservice.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByUsername(String username);

}
