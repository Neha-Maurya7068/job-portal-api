package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDTO {

    private Long id;

    private String userEmail;

    private String action;

    private String details;

    private String ipAddress;

    private String httpMethod;

    private String requestUri;

    private Integer statusCode;

    private LocalDateTime createdAt;
}