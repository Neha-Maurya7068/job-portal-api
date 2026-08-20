package com.neha.job_portal_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Public APIs
                .requestMatchers(
                    "/api/users/register",
                    "/api/users/login"
                ).permitAll()

                // JWT authenticated
                .requestMatchers("/api/whoami")
                .authenticated()

                // Role based APIs
                .requestMatchers("/api/admin")
                .hasRole("ADMIN")

                .requestMatchers("/api/recruiter")
                .hasRole("RECRUITER")

                .requestMatchers("/api/jobseeker")
                .hasRole("JOB_SEEKER")

                // Jobs
                .requestMatchers(HttpMethod.GET, "/api/jobs")
                .hasAnyRole("RECRUITER", "JOB_SEEKER", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/jobs")
                .hasRole("RECRUITER")

                .requestMatchers(HttpMethod.PUT, "/api/jobs/**")
                .hasRole("RECRUITER")

                .requestMatchers(HttpMethod.DELETE, "/api/jobs/**")
                .hasRole("RECRUITER")
                
             // Applications

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/applications"
                ).hasRole("JOB_SEEKER")

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/applications/my"
                ).hasRole("JOB_SEEKER")

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/applications"
                ).hasRole("RECRUITER")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/applications/*/status"
                ).hasRole("RECRUITER")
                
                
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/applications/recent"
                ).hasRole("RECRUITER")
                
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/applications/job/*/count"
                ).hasRole("RECRUITER")
                
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/jobs/my"
                ).hasRole("RECRUITER")
                
                // Everything else
                .anyRequest()
                .authenticated()
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}