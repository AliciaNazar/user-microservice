package com.mindhub.user_microservice.services.impl;

import com.mindhub.user_microservice.config.RabbitMQConfig;
import com.mindhub.user_microservice.dtos.UserDTO;
import com.mindhub.user_microservice.dtos.UserDTORequest;
import com.mindhub.user_microservice.events.EmailEvent;
import com.mindhub.user_microservice.exceptions.CustomException;
import com.mindhub.user_microservice.models.RolType;
import com.mindhub.user_microservice.models.UserEntity;
import com.mindhub.user_microservice.repositories.UserRepository;
import com.mindhub.user_microservice.services.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.core.*;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue userRegisteredQueue;

    @Override
    public List<UserDTO> getUsers() {
        List<UserEntity> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(user))
                .toList();
        return userDTOs;
    }

    @Override
    public UserDTO createUser(UserDTORequest userDTORequest) {
        createUserValidations(userDTORequest);

        UserEntity user = new UserEntity();
        user.setUsername(userDTORequest.getUsername());
        user.setEmail(userDTORequest.getEmail());
        user.setRoles(userDTORequest.getRoles());
        user = this.userRepository.save(user);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "user.email",
                new EmailEvent(user.getEmail(), "Bienvenida/o a nuestra plataforma", "Gracias por registrarte " + user.getUsername()));


        return new UserDTO(user);
    }

    @Override
    public List<String> getRoles() {
        return Arrays.stream(RolType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id){
        this.userRepository.findById(id);
        this.userRepository.deleteById(id);
    }


    private void createUserValidations(UserDTORequest userDTORequest){
        usernameValidations(userDTORequest.getUsername());
        emailValidations(userDTORequest.getEmail());
    }

    private void usernameValidations(String username){
        if (username.isBlank()) {
            throw new CustomException("Username can't be empty.");
        }
    }

    private void emailValidations(String email){
        if (email.isBlank()){
            throw new CustomException("Email can't be empty.");
        }
        if (this.userRepository.existsByEmail(email)){
            throw new CustomException("This email is already registered",HttpStatus.CONFLICT);
        }
        if (!email.contains("@") || !email.contains(".")){
            throw new CustomException("Invalid email format.");
        }
    }






    @Override
    public UserEntity getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException("User not found",HttpStatus.NOT_FOUND));
        return user;
    }
    @Override
    public UserEntity getUserById(Long id) {
        UserEntity user = userRepository.getUserById(id)
                .orElseThrow(()->new CustomException("User not found",HttpStatus.NOT_FOUND));
        return user;
    }



}
