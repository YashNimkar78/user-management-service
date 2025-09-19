package com.service.usermanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.usermanagement.config.JwtUtil;
import com.service.usermanagement.entity.Role;
import com.service.usermanagement.entity.User;
import com.service.usermanagement.model.AuthRequest;
import com.service.usermanagement.model.RegisterRequest;
import com.service.usermanagement.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@MockitoBean
	private AuthenticationManager authenticationManager;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtUtil jwtUtil;

	// Positive Test - Register a user successfully
	@Test
	void testRegister_Success() throws Exception {
		RegisterRequest req = new RegisterRequest();
		req.setEmail("test@example.com");
		req.setPassword("password123");
		req.setFullName("Test User");
		req.setRole("ROLE_ADMIN");
		User savedUser = new User();
		savedUser.setId(1L);
		savedUser.setEmail("test@example.com");
		savedUser.setFullName("Test User");
		Role adminRole = new Role("ROLE_ADMIN");
		savedUser.setRoles(Set.of(adminRole));
		when(userService.register(any(RegisterRequest.class))).thenReturn(savedUser);

		mockMvc.perform(
				post("/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.email").value("test@example.com"))
				.andExpect(jsonPath("$.fullName").value("Test User"));
	}


	// Positive Test - Login successfully
	@Test
	void testLogin_Success() throws Exception {
		AuthRequest req = new AuthRequest();
		req.setEmail("user@example.com");
		req.setPassword("password123");

		Authentication auth = Mockito.mock(Authentication.class);

		// Correct Spring Security User
		org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
				"user@example.com", "password123", List.of(new SimpleGrantedAuthority("ROLE_USER")));

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
		when(auth.getPrincipal()).thenReturn(principal);

		when(jwtUtil.generateToken("user@example.com", List.of("ROLE_USER"))).thenReturn("mocked-jwt-token");

		mockMvc.perform(
				post("/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.token").value("mocked-jwt-token"));
	}

	
}
