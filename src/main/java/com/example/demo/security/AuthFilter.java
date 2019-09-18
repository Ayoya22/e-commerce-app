/**
 * 
 */
package com.example.demo.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


public class AuthFilter extends UsernamePasswordAuthenticationFilter {

	/** The authentication manager. */
	private AuthenticationManager authenticationManager;


	public AuthFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}


	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {
			CreateUserRequest creds = new ObjectMapper().readValue(req.getInputStream(), CreateUserRequest.class);
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(),
					creds.getPassword(), new ArrayList<>()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		String token = Jwts.builder().setSubject(((User) auth.getPrincipal()).getUsername())
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET).compact();
		res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
	}
}
