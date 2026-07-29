package com.neha.job_portal_api.service.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private long jwtExpiration;
	
	private Key getSignInKey() {
	    return Keys.hmacShaKeyFor(secretKey.getBytes());
	}
	
	public String generateToken(String email) {

	    return Jwts.builder()
	            .subject(email)
	            .issuedAt(new Date())
	            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
	            .signWith(getSignInKey())
	            .compact();
	}
	
}
