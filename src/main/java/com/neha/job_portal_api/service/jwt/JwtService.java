package com.neha.job_portal_api.service.jwt;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private long jwtExpiration;
	
	private SecretKey getSignInKey() {
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
	
	public String extractUsername(String token) {
	    return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

	    final Claims claims = extractAllClaims(token);

	    return claimsResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {

	    return Jwts.parser()
	            .verifyWith(getSignInKey())
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();
	}
	
	public Date extractExpiration(String token) {
	    return extractClaim(token, Claims::getExpiration);
	}
	
	private boolean isTokenExpired(String token) {
	    return extractExpiration(token).before(new Date());
	}
	
	public boolean isTokenValid(String token, String email) {

	    final String username = extractUsername(token);

	    return username.equals(email) && !isTokenExpired(token);
	}
	
}
