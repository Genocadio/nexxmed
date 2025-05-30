package com.nexxserve.users.controller;

import com.nexxserve.users.dto.UserDTO;
import com.nexxserve.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
//    @PreAuthorize("hasAuthority('view_user_data')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    @GetMapping
//    @PreAuthorize("hasAuthority('view_users')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('create_user')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
//    @PreAuthorize("hasAuthority('update_user') or #userId == authentication.principal")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String userId, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    @DeleteMapping("/{userId}")
//    @PreAuthorize("hasAuthority('delete_user')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // Public endpoint for internal service communication
    @GetMapping("/public/{userId}")
    public ResponseEntity<UserDTO> getUserByIdPublic(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}