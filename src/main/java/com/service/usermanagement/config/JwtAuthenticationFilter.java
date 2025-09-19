package com.service.usermanagement.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String header = request.getHeader("Authorization");
		String token = null;
			if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
			token = header.substring(7);
		}

		if (token != null && jwtUtil.validateToken(token)) {
			String username = jwtUtil.getUsernameFromToken(token);
			// extract roles claim
			List<String> roleNames = jwtUtil.getClaimFromToken(token, c -> c.get("roles", List.class));
			List<GrantedAuthority> authorities = new ArrayList<>();
			if (roleNames != null) {
				roleNames.forEach(r -> authorities.add(new SimpleGrantedAuthority(r)));
			}
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
					authorities);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		filterChain.doFilter(request, response);
	}
}
