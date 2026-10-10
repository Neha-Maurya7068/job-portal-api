package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.job_portal_api.dto.AssignedSecurityEventSummaryDTO;
import com.neha.job_portal_api.dto.SecurityDashboardDTO;
import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.entity.SecurityEvent;
import com.neha.job_portal_api.repository.LoginActivityRepository;
import com.neha.job_portal_api.repository.LoginAttemptRepository;
import com.neha.job_portal_api.repository.SecurityEventRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.SecurityDashboardService;
import com.neha.job_portal_api.service.SecurityEventService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityDashboardServiceImpl
        implements SecurityDashboardService {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final LoginActivityRepository loginActivityRepository;
    private final SecurityEventRepository securityEventRepository;
    private final SecurityEventService securityEventService;

    @Override
    @Transactional(readOnly = true)
    public SecurityDashboardDTO getDashboard() {

        LocalDateTime fifteenMinutesAgo =
                LocalDateTime.now().minusMinutes(15);

        long totalUsers = userRepository.count();

        long lockedAccounts =
                userRepository.countByLockedUntilAfter(
                        LocalDateTime.now());

        long failedLoginAttempts =
                loginAttemptRepository
                        .countBySuccessFalseAndAttemptedAtAfter(
                                fifteenMinutesAgo);

        long activeSessions =
                loginActivityRepository.countByActiveTrue();

        long suspiciousEvents =
                securityEventRepository.countByResolvedFalse();

        long totalSecurityEvents =
                securityEventRepository.count();

        // Latest 10 security events across the system
        List<SecurityEventDTO> recentEvents =
                securityEventRepository
                        .findTop10ByOrderByCreatedAtDesc()
                        .stream()
                        .map(this::mapToDTO)
                        .toList();

        // Summary for the currently authenticated admin
        AssignedSecurityEventSummaryDTO assignedSummary =
                securityEventService.getMyAssignedEventSummary();

        // Assigned investigation queue for the current admin
        List<SecurityEventDTO> assignedEvents =
                securityEventService.getMyAssignedEvents()
                        .stream()
                        .limit(10)
                        .toList();

        return SecurityDashboardDTO.builder()
                .totalUsers(totalUsers)
                .lockedAccounts(lockedAccounts)
                .failedLoginAttempts(failedLoginAttempts)
                .activeSessions(activeSessions)
                .suspiciousEvents(suspiciousEvents)
                .recentSecurityEvents(recentEvents)
                .totalSecurityEvents(totalSecurityEvents)
                .assignedEventSummary(assignedSummary)
                .assignedSecurityEvents(assignedEvents)
                .build();
    }

    private SecurityEventDTO mapToDTO(SecurityEvent event) {

        return SecurityEventDTO.builder()
                .id(event.getId())
                .userEmail(event.getEmail())
                .type(event.getType())
                .description(event.getDescription())
                .ipAddress(event.getIpAddress())
                .userAgent(event.getUserAgent())
                .createdAt(event.getCreatedAt())
                .resolved(event.isResolved())
                .build();
    }
}