package com.mindhub.user_microservice.services;

import com.mindhub.user_microservice.dtos.*;
import com.mindhub.user_microservice.exceptions.CustomException;
import com.mindhub.user_microservice.models.RolType;
import com.mindhub.user_microservice.models.UserEntity;

import java.util.List;

public interface UserService {

    List<UserDTO> getUsers();
    UserDTO createUser(RegisterUserDTO registerUserDTO);
    List<String> getRoles();
    void deleteUser(Long id);
    String loginUser(LoginUserDTO loginUserDTO);


    UserRegistrationDTO getUserByEmail(String email);
    UserEntity getUserById(Long id);
    UserEntity getUserByUsername(String username);

    void validateUser(Long id);
    UserDTO updateUser (Long id, UpdateUserDTO updateUserDTO);
}
