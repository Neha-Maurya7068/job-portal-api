package com.neha.job_portal_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "login_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    private LocalDateTime attemptedAt;

    private boolean success;

    private String failureReason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}