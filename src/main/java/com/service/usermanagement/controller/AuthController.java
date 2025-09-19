package com.service.usermanagement.controller;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.service.usermanagement.config.JwtUtil;
import com.service.usermanagement.entity.User;
import com.service.usermanagement.model.AuthRequest;
import com.service.usermanagement.model.AuthResponse;
import com.service.usermanagement.model.RegisterRequest;
import com.service.usermanagement.service.UserService;

import jakarta.validation.Valid;

@RestController
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final JwtUtil jwtUtil;

	public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
		User saved = userService.register(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
		Authentication auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth
				.getPrincipal();

		var roles = principal.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList());

		String token = jwtUtil.generateToken(principal.getUsername(), roles);
		return ResponseEntity.ok(new AuthResponse(token));
	}
}
