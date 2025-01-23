package com.mindhub.user_microservice.controllers;

import com.mindhub.user_microservice.dtos.UserDTO;
import com.mindhub.user_microservice.dtos.UserDTORequest;
import com.mindhub.user_microservice.services.UserService;
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

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers(){
        List<UserDTO> users = this.userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTORequest userDTORequest){
        UserDTO userDTO = this.userService.createUser(userDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = userService.getRoles();
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
