
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
    private final AuditLogFilter auditLogFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Public APIs
                .requestMatchers(
                    "/api/users/register",
                    "/api/users/login",
                    "/api/password/forgot",
                    "/api/password/reset"
                ).permitAll()

                // Authenticated APIs
                .requestMatchers("/api/whoami")
                    .authenticated()

                .requestMatchers("/api/login-activity/**")
                    .authenticated()

                // Audit logs
                .requestMatchers("/api/audit-logs/my")
                    .authenticated()

                .requestMatchers("/api/audit-logs/**")
                    .hasRole("ADMIN")

                // Security events
                .requestMatchers("/api/security-events/my")
                    .hasAnyRole(
                        "ADMIN",
                        "RECRUITER",
                        "JOB_SEEKER"
                    )

                .requestMatchers("/api/security-events/**")
                    .hasRole("ADMIN")

                // Job seeker profile and resume
                .requestMatchers("/api/profile/**")
                    .hasRole("JOB_SEEKER")

                .requestMatchers("/api/resume/**")
                    .hasRole("JOB_SEEKER")

                .requestMatchers("/api/jobseeker")
                    .hasRole("JOB_SEEKER")

                .requestMatchers("/api/saved-jobs/**")
                    .hasRole("JOB_SEEKER")

                .requestMatchers("/api/jobs/recommendations")
                    .hasRole("JOB_SEEKER")

                // Admin APIs
                .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")

                // Recruiter analytics
                .requestMatchers("/api/recruiter/analytics/**")
                    .hasRole("RECRUITER")

                // Jobs: read
                .requestMatchers(HttpMethod.GET, "/api/jobs")
                    .hasAnyRole(
                        "ADMIN",
                        "RECRUITER",
                        "JOB_SEEKER"
                    )

                // Jobs: create
                .requestMatchers(HttpMethod.POST, "/api/jobs")
                    .hasRole("RECRUITER")

                // Jobs: update
                .requestMatchers(HttpMethod.PUT, "/api/jobs/**")
                    .hasRole("RECRUITER")

                // Jobs: delete
                .requestMatchers(HttpMethod.DELETE, "/api/jobs/**")
                    .hasRole("RECRUITER")

                // Applications: create
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/applications"
                ).hasRole("JOB_SEEKER")

                // Job seeker: own applications
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications/my"
                ).hasRole("JOB_SEEKER")

                // Recruiter: view applications
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications"
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
                    "/api/applications/job/**"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/jobs/my"
                ).hasRole("RECRUITER")

                // Recruiter: update application status
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/applications/*/status"
                ).hasRole("RECRUITER")

                // Recruiter: delete applications
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/applications/**"
                ).hasRole("RECRUITER")

                // All remaining APIs require authentication
                .anyRequest().authenticated()
            )

            // JWT-based stateless authentication
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // JWT filter runs before username/password filter
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            // Audit filter runs after JWT authentication filter
            .addFilterAfter(
                auditLogFilter,
                JwtAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
