package com.service.usermanagement.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.service.usermanagement.entity.Role;
import com.service.usermanagement.entity.User;
import com.service.usermanagement.exceptionhandler.ResourceNotFoundException;
import com.service.usermanagement.model.RegisterRequest;
import com.service.usermanagement.repository.RoleRepository;
import com.service.usermanagement.repository.UserRepository;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepo, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest req){
        if(userRepository.existsByEmail(req.getEmail())){
            throw new IllegalArgumentException("Email already in use");
        }
        String roleName = req.getRole() == null ? "ROLE_USER" : req.getRole();
        Role role = roleRepo.findById(roleName).orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        User u = new User();
        u.setEmail(req.getEmail());
        u.setFullName(req.getFullName());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRoles(new HashSet<>(Collections.singletonList(role)));
        return userRepository.save(u);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public void deleteUser(Long id){
        if(!userRepository.existsById(id)) throw new ResourceNotFoundException("User", "id", id);
        userRepository.deleteById(id);
    }
}

