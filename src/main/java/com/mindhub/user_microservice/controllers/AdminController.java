package com.mindhub.user_microservice.controllers;

import com.mindhub.user_microservice.dtos.UserDTO;
import com.mindhub.user_microservice.dtos.UserRegistrationDTO;
import com.mindhub.user_microservice.exceptions.CustomException;
import com.mindhub.user_microservice.models.UserEntity;
import com.mindhub.user_microservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Retrieve a list of all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully."),
    })
    @GetMapping("users/all")
    public ResponseEntity<List<UserDTO>> getUsers(){
        List<UserDTO> users = this.userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get a user by ID", description = "Retrieves a user by the specified id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized: The user is not authenticated or the provided credentials are invalid."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        UserDTO userDTO = new UserDTO(this.userService.getUserById(id));
        return ResponseEntity.ok(userDTO);
    }


    @Operation(summary = "Delete a user by ID", description = "Deletes an existing user by the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @DeleteMapping("user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get User ID by Email", description = "Retrieves the ID of a user based on the provided email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User ID retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "User with the given email not found."),
            @ApiResponse(responseCode = "400", description = "Invalid email format.")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Long> getUserByEmail(@PathVariable String email) throws CustomException {
        UserRegistrationDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user.getId());
    }

    @Operation(summary = "Get user email by the provided id", description = "Retrieves the email of a user based on the provided id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "User with the given id not found."),
            @ApiResponse(responseCode = "400", description = "Invalid id format.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<String> getEmailById(@PathVariable Long id) {
        UserEntity user = userService.getUserById(id);
        return ResponseEntity.ok(user.getEmail());
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

}
