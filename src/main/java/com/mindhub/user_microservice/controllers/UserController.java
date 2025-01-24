package com.mindhub.user_microservice.controllers;

import com.mindhub.user_microservice.dtos.UserDTO;
import com.mindhub.user_microservice.dtos.UserDTORequest;
import com.mindhub.user_microservice.exceptions.CustomException;
import com.mindhub.user_microservice.models.UserEntity;
import com.mindhub.user_microservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Retrieve a list of all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully."),
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers(){
        List<UserDTO> users = this.userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Register a new user", description = "Registers a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
    })
    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTORequest userDTORequest){
        UserDTO userDTO = this.userService.createUser(userDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @Operation(summary = "Get all roles", description = "Retrieve a list of all roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully."),
    })
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = userService.getRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Delete a user by ID", description = "Deletes an existing user by the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @DeleteMapping("users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("users/email/{email}")
    public ResponseEntity<Long> getUserByEmail(@PathVariable String email) throws CustomException {
        UserEntity user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user.getId());
    }


}
