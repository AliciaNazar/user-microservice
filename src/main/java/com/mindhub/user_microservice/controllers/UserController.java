package com.mindhub.user_microservice.controllers;

import com.mindhub.user_microservice.config.JwtUtils;
import com.mindhub.user_microservice.dtos.RegisterUserDTO;
import com.mindhub.user_microservice.dtos.UpdateUserDTO;
import com.mindhub.user_microservice.dtos.UserDTO;
import com.mindhub.user_microservice.dtos.UserDTORequest;
import com.mindhub.user_microservice.exceptions.CustomException;
import com.mindhub.user_microservice.models.UserEntity;
import com.mindhub.user_microservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private JwtUtils jwtUtils;


    @Operation(summary = "Get authenticated user", description = "Retrieves the currently authenticated user's details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized: The user is not authenticated.")
    })
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUser(HttpServletRequest request){
        String email = jwtUtils.getEmailFromToken(request.getHeader("Authorization"));
        UserDTO userDTO = new UserDTO(this.userService.getUserByEmail(email));
        return ResponseEntity.ok(userDTO);
    }


    @Operation(summary = "Delete authenticated user", description = "Deletes the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized: The user is not authenticated."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @DeleteMapping()
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        String email = jwtUtils.getEmailFromToken(request.getHeader("Authorization"));
        Long id = this.userService.getUserByEmail(email).getId();
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Update authenticated user", description = "Updates the details of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "401", description = "Unauthorized: The user is not authenticated.")
    })
    @PutMapping()
    public ResponseEntity<UserDTO> updateUser(@RequestBody UpdateUserDTO updateUserDTO, HttpServletRequest request) {
        String email = jwtUtils.getEmailFromToken(request.getHeader("Authorization"));
        Long id = this.userService.getUserByEmail(email).getId();
        UserDTO updatedUser = this.userService.updateUser(id, updateUserDTO);
        return ResponseEntity.ok(updatedUser);

    }


}
