package com.neha.job_portal_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "security_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    private SecurityEventType type;

    @Column(length = 1000)
    private String description;

    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    private LocalDateTime createdAt;

    private boolean resolved = false;

    // NEW
    private LocalDateTime resolvedAt;

    // NEW
    @ManyToOne
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
}