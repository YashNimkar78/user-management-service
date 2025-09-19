package com.service.usermanagement.config;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

	private JwtUtil jwtUtil;
	private final String secret = "MySuperSecretKeyForJwtMySuperSecretKeyForJwt"; // must be at least 32 chars
	private final long expirationMs = 3600000; // 1 hour

	@BeforeEach
	void setUp() {
		jwtUtil = new JwtUtil(secret, expirationMs);
	}

	@Test
	void testGenerateTokenAndValidate() {
		String username = "user@example.com";
		List<String> roles = List.of("ROLE_USER");

		// Generate token
		String token = jwtUtil.generateToken(username, roles);
		assertNotNull(token);

		// Validate token
		assertTrue(jwtUtil.validateToken(token));

		// Extract username
		String extractedUsername = jwtUtil.getUsernameFromToken(token);
		assertEquals(username, extractedUsername);

		// Extract roles from claims
		@SuppressWarnings("unchecked")
		List<String> extractedRoles = jwtUtil.getClaimFromToken(token, claims -> (List<String>) claims.get("roles"));
		assertEquals(roles, extractedRoles);
	}

	@Test
	void testInvalidToken() {
		String invalidToken = "invalid.jwt.token";

		// Validation should fail
		assertFalse(jwtUtil.validateToken(invalidToken));

		// Trying to extract username should throw JwtException
		assertThrows(JwtException.class, () -> jwtUtil.getUsernameFromToken(invalidToken));
	}

	@Test
	void testExpiredToken() throws InterruptedException {
		// Short-lived token for testing expiry
		JwtUtil shortLivedJwtUtil = new JwtUtil(secret, 100); // 100ms
		String token = shortLivedJwtUtil.generateToken("user@example.com", List.of("ROLE_USER"));

		// Wait for token to expire
		Thread.sleep(200);

		// Token should now be invalid
		assertFalse(shortLivedJwtUtil.validateToken(token));
		assertThrows(JwtException.class, () -> shortLivedJwtUtil.getUsernameFromToken(token));
	}
}
