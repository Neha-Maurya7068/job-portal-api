package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import com.neha.job_portal_api.entity.SecurityEventType;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityEventDTO {

    private Long id;

    private String userEmail;

    private SecurityEventType type;

    private String description;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime createdAt;

    private boolean resolved;
}