package com.service.usermanagement.config;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final Key key;
	private final long jwtExpirationMs;

	public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expirationMs}") long jwtExpirationMs) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.jwtExpirationMs = jwtExpirationMs;
	}

	public String generateToken(String username, Collection<String> roles) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtExpirationMs);
		return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(now).setExpiration(expiry)
				.signWith(key).compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException ex) {
			return false;
		}
	}

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claimsResolver.apply(claims);
	}
}
