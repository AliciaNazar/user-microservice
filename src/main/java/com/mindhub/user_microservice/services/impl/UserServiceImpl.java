package com.mindhub.user_microservice.services.impl;

//import com.mindhub.user_microservice.config.JwtUtils;
import com.mindhub.user_microservice.config.JwtUtils;
import com.mindhub.user_microservice.config.RabbitMQConfig;
import com.mindhub.user_microservice.dtos.*;
import com.mindhub.user_microservice.events.EmailEvent;
import com.mindhub.user_microservice.exceptions.CustomException;
import com.mindhub.user_microservice.models.RolType;
import com.mindhub.user_microservice.models.UserEntity;
import com.mindhub.user_microservice.models.UserStatus;
import com.mindhub.user_microservice.repositories.UserRepository;
import com.mindhub.user_microservice.services.UserService;
import jdk.jshell.spi.ExecutionControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.core.*;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService{

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue userRegisteredQueue;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public List<UserDTO> getUsers() {
        List<UserEntity> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(user))
                .toList();
        return userDTOs;
    }

    private void sendRegistrationEmail(UserEntity userEntity){
        String secret= "aG9sYWJ1ZW5kaWFjb21vdmF5b3NveWFsaWNpYW5hemFy";
        String jwt = jwtUtils.generateRegisterToken(userEntity.getId(), secret);
        String confirmationLink = "http://localhost:8080/api/auth/register/" + jwt;

        EmailEvent emailEvent = new EmailEvent(
                userEntity.getEmail(),
                "Welcome to our platform!",
                confirmationLink,  // Solo enviamos el link
                userEntity.getUsername() // Nuevo campo para el username
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "user.email", emailEvent);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public UserDTO createUser(RegisterUserDTO registerUserDTO) {
        try{
            createUserValidations(registerUserDTO);

            UserEntity user = new UserEntity();
            user.setUsername(registerUserDTO.getUsername());
            user.setEmail(registerUserDTO.getEmail());
            user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
            user.setRoles(registerUserDTO.getRoles());
            user = this.userRepository.save(user);
            sendRegistrationEmail(user);

            log.info("User {} register successfully", user.getEmail());
            return new UserDTO(user);
        } catch (Exception e) {
            log.error("Error registering user: {}", registerUserDTO.getEmail(), e);
            throw new CustomException("Error registering user", HttpStatus.INTERNAL_SERVER_ERROR);
        }

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


    private void createUserValidations(RegisterUserDTO registerUserDTO){
        usernameValidations(registerUserDTO.getUsername());
        emailValidations(registerUserDTO.getEmail());
    }

    private void usernameValidations(String username){
        if (username.isBlank()) {
            log.error("Username can't be empty.");
            throw new CustomException("Username can't be empty.");
        }
    }

    private void passwordValidations(String password){
        if (password ==null||password.isBlank()){
            log.error("Password can't be empty.");
            throw new CustomException("Password can't be empty.");
        }
    }

    private void emailValidations(String email){
        if (email.isBlank()){
            log.error("Email validation failed: empty email.");
            throw new CustomException("Email can't be empty.");
        }
        if (this.userRepository.existsByEmail(email)){
            log.error("Email validation failed: email {} is already registered.", email);
            throw new CustomException("This email is already registered",HttpStatus.CONFLICT);
        }
        if (!email.contains("@") || !email.contains(".")){
            log.error("Email validation failed: invalid email format ({})", email);
            throw new CustomException("Invalid email format.");
        }
    }



    @Override
    public UserRegistrationDTO getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new CustomException("User not found.", HttpStatus.NOT_FOUND));
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(user.getId(), user.getUsername(),user.getEmail(),user.getRoles(), user.getUserStatus());
        return userRegistrationDTO;
    }


    @Override
    public UserEntity getUserById(Long id) {
        UserEntity user = userRepository.getUserById(id)
                .orElseThrow(()->new CustomException("User not found",HttpStatus.NOT_FOUND));
        return user;
    }
    @Override
    public UserEntity getUserByUsername(String username) {
        UserEntity user = userRepository.getUserByUsername(username)
                .orElseThrow(()->new CustomException("User not found",HttpStatus.NOT_FOUND));
        return user;
    }


    @Override
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO){
        usernameValidations(updateUserDTO.getUsername());
        passwordValidations(updateUserDTO.getPassword());
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User nor found.",HttpStatus.NOT_FOUND));
        user.setUsername(updateUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        user = this.userRepository.save(user);
        return new UserDTO(user);
    }




    @Override
    public String loginUser(LoginUserDTO loginUserDTO){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUserDTO.getEmail(),
                            loginUserDTO.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserRegistrationDTO user = getUserByEmail(loginUserDTO.getEmail());

            if (user.getUserStatus() == UserStatus.ACTIVE) {
                String jwt = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole().toString());
                return jwt;
            } else {
                throw new CustomException("User is not active.", HttpStatus.UNAUTHORIZED);
            }
        }catch (BadCredentialsException e){
            throw new CustomException("Bad credentials exception.",HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void validateUser(Long id){
        UserEntity user = userRepository.findById(id).orElseThrow(()->new CustomException("User not found.", HttpStatus.NOT_FOUND));
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }



}
