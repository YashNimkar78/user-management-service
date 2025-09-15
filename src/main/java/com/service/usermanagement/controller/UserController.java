package com.service.usermanagement.controller;




import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.service.usermanagement.entity.User;
import com.service.usermanagement.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){ this.userService = userService; }

    @GetMapping
    public List<User> getAllUsers(){ return userService.getAllUsers(); }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id){ return userService.getUser(id); }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
