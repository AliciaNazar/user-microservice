package com.mindhub.user_microservice.controllers;

import com.mindhub.user_microservice.config.JwtUtils;
import com.mindhub.user_microservice.dtos.LoginUserDTO;
import com.mindhub.user_microservice.dtos.RegisterUserDTO;
import com.mindhub.user_microservice.dtos.UserDTO;
import com.mindhub.user_microservice.dtos.UserDTORequest;
import com.mindhub.user_microservice.models.RolType;
import com.mindhub.user_microservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private UserService userService;

    @Operation(summary = "User Login", description = "Authenticates a user by username and password, and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully, JWT token returned."),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Incorrect username or password."),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginUserDTO loginRequest) {
        String token = userService.loginUser(loginRequest);
        return ResponseEntity.ok(token);
    }




    @Operation(summary = "Register a new user", description = "Registers a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
    })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@RequestBody RegisterUserDTO registerUserDTO){
        UserDTO userDTO = this.userService.createUser(registerUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }


    @GetMapping("/register/{confirmationToken}")
    public ResponseEntity<String> confirmUser(@PathVariable String confirmationToken) {
        Long id = Long.parseLong(jwtUtil.extractEmail(confirmationToken));
        userService.validateUser(id);
        return ResponseEntity.ok("The user was confirmed");
    }
}