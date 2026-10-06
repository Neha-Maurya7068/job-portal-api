package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginActivityDTO {

    private Long id;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime loginAt;

    private LocalDateTime logoutAt;

    private LocalDateTime expiresAt;

    private boolean active;
}