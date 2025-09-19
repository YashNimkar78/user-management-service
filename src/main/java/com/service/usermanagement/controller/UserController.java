package com.service.usermanagement.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.usermanagement.entity.User;
import com.service.usermanagement.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	// ADMIN only (enforced in SecurityConfig)
	@GetMapping
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUser(@PathVariable Long id, Principal principal, Authentication authentication) {
		String loggedInEmail = authentication.getName();
		boolean isUserRole = authentication.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

		// If ROLE_USER tries to access *anyone else's* account - 403 Forbidden
		if (isUserRole) {
			User self = userService.getUser(id);

			// If the user exists but it's not the same logged-in email - forbidden
			if (self == null || !self.getEmail().equals(loggedInEmail)) {
				return ResponseEntity.status(403).build(); // Always 403 for normal users
			}

			// User accessing their own account - 200 OK
			return ResponseEntity.ok(self);
		}

		// For ADMIN (or others) - check if user exists
		User user = userService.getUser(id);
		if (user == null) {
			return ResponseEntity.notFound().build(); // 404 Not Found
		}

		return ResponseEntity.ok(user); // 200 Ok
	}

	// ADMIN only (enforced in SecurityConfig)
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);

		return ResponseEntity.noContent().build();
	}
}
