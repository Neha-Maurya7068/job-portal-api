package com.neha.job_portal_api.service.jwt;

import java.util.Date;
import java.util.UUID;
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
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes()
        );
    }

    public String generateToken(String email) {

        String tokenId = UUID.randomUUID().toString();

        return Jwts.builder()
                .subject(email)
                .id(tokenId)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    public String extractTokenId(String token) {

        return extractClaim(
                token,
                Claims::getId
        );
    }

    public Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        final Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    public boolean isTokenValid(
            String token,
            String username) {

        final String extractedUsername =
                extractUsername(token);

        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }
}