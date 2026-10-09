package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import com.neha.job_portal_api.entity.SecurityEventActionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityEventActionDTO {

    private Long id;

    private Long securityEventId;

    private SecurityEventActionType action;

    private String performedBy;

    private String note;

    private LocalDateTime performedAt;
}