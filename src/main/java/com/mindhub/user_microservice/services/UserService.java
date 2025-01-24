package com.mindhub.user_microservice.services;

import com.mindhub.user_microservice.dtos.UserDTO;
import com.mindhub.user_microservice.dtos.UserDTORequest;
import com.mindhub.user_microservice.exceptions.CustomException;
import com.mindhub.user_microservice.models.RolType;
import com.mindhub.user_microservice.models.UserEntity;

import java.util.List;

public interface UserService {

    List<UserDTO> getUsers();
    UserDTO createUser(UserDTORequest userDTORequest);
    List<String> getRoles();
    void deleteUser(Long id);



    UserEntity getUserByEmail(String email);
}
