package com.neha.job_portal_api.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.neha.job_portal_api.entity.LoginActivity;
import com.neha.job_portal_api.repository.LoginActivityRepository;
import com.neha.job_portal_api.service.jwt.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final LoginActivityRepository
            loginActivityRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            final String jwt =
                    authHeader.substring(7);

            final String userEmail =
                    jwtService.extractUsername(jwt);

            System.out.println(
                    "JWT EMAIL = " + userEmail);

            if (userEmail != null
                    && SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(
                                        userEmail);

                boolean validToken =
                        jwtService.isTokenValid(
                                jwt,
                                userDetails.getUsername());

                if (!validToken) {

                    System.out.println(
                            "JWT TOKEN INVALID");

                    filterChain.doFilter(
                            request,
                            response);

                    return;
                }

                String tokenId =
                        jwtService.extractTokenId(jwt);

                System.out.println(
                        "TOKEN ID = " + tokenId);

                Optional<LoginActivity> activeSession =
                        loginActivityRepository
                                .findByTokenIdAndActiveTrue(
                                        tokenId);

                if (activeSession.isEmpty()) {

                    System.out.println(
                            "LOGIN SESSION INACTIVE");

                    filterChain.doFilter(
                            request,
                            response);

                    return;
                }

                UsernamePasswordAuthenticationToken
                        authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);

                System.out.println(
                        "AUTHORITIES = "
                                + userDetails
                                        .getAuthorities());

                System.out.println(
                        "REQUEST = "
                                + request.getMethod()
                                + " "
                                + request.getRequestURI());

                System.out.println(
                        "AUTHENTICATED = "
                                + SecurityContextHolder
                                        .getContext()
                                        .getAuthentication()
                                        .isAuthenticated());
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT AUTHENTICATION ERROR = "
                            + e.getMessage());

            SecurityContextHolder
                    .clearContext();
        }

        filterChain.doFilter(request, response);
    }
}