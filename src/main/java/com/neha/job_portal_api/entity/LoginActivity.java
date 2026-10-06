package com.neha.job_portal_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "login_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tokenId;

    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    private LocalDateTime loginAt;

    private LocalDateTime logoutAt;

    private LocalDateTime expiresAt;

    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}