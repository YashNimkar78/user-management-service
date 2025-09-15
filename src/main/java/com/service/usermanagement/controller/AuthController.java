package com.service.usermanagement.controller;




import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.service.usermanagement.config.JwtUtil;
import com.service.usermanagement.entity.User;
import com.service.usermanagement.model.AuthRequest;
import com.service.usermanagement.model.AuthResponse;
import com.service.usermanagement.model.RegisterRequest;
import com.service.usermanagement.repository.UserRepository;
import com.service.usermanagement.service.UserService;

import java.util.stream.Collectors;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtUtil jwtUtil,
                          UserRepository userRepository){
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req){
        User saved = userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req){
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();

        var roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority()).collect(Collectors.toList());

        String token = jwtUtil.generateToken(principal.getUsername(), roles);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
