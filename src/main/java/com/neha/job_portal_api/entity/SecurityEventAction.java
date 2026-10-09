package com.neha.job_portal_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "security_event_actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityEventAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "security_event_id",
            nullable = false
    )
    private SecurityEvent securityEvent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SecurityEventActionType action;

    @ManyToOne
    @JoinColumn(
            name = "performed_by",
            nullable = false
    )
    private User performedBy;

    @Column(length = 1000)
    private String note;

    @Column(nullable = false)
    private LocalDateTime performedAt;
}