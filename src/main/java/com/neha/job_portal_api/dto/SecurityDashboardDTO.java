package com.neha.job_portal_api.dto;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityDashboardDTO {

    private long totalUsers;

    private long lockedAccounts;

    private long failedLoginAttempts;

    private long activeSessions;

    private long suspiciousEvents;

    private List<SecurityEventDTO> recentSecurityEvents;
}