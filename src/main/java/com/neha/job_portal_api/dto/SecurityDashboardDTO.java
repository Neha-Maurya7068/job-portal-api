package com.neha.job_portal_api.dto;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityDashboardDTO {

    // Existing dashboard metrics
    private long totalUsers;
    private long lockedAccounts;
    private long failedLoginAttempts;
    private long activeSessions;
    private long suspiciousEvents;

    // Existing recent alerts
    private List<SecurityEventDTO> recentSecurityEvents;

    // New: overall security-event count
    private long totalSecurityEvents;

    // New: assigned events priority summary
    private AssignedSecurityEventSummaryDTO assignedEventSummary;

    // New: investigation queue for logged-in admin
    private List<SecurityEventDTO> assignedSecurityEvents;
    
    
}