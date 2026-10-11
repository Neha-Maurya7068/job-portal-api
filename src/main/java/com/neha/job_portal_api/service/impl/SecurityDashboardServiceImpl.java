
package com.neha.job_portal_api.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.job_portal_api.dto.AssignedSecurityEventSummaryDTO;
import com.neha.job_portal_api.dto.SecurityDashboardDTO;
import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.dto.SecurityTrendPointDTO;
import com.neha.job_portal_api.entity.SecurityEvent;
import com.neha.job_portal_api.entity.SecurityEventType;
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

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fifteenMinutesAgo = now.minusMinutes(15);

        // Total registered users
        long totalUsers = userRepository.count();

        // Accounts currently locked
        long lockedAccounts =
                userRepository.countByLockedUntilAfter(now);

        // Failed login attempts during the last 15 minutes
        long failedLoginAttempts =
                loginAttemptRepository
                        .countBySuccessFalseAndAttemptedAtAfter(
                                fifteenMinutesAgo
                        );

        // Currently active login sessions
        long activeSessions =
                loginActivityRepository.countByActiveTrue();

        // All unresolved security events
        long unresolvedSecurityEvents =
                securityEventRepository.countByResolvedFalse();

        // Event types considered suspicious by this application
        List<SecurityEventType> suspiciousTypes = List.of(
                SecurityEventType.MULTIPLE_FAILED_LOGINS,
                SecurityEventType.NEW_IP_LOGIN,
                SecurityEventType.NEW_DEVICE_LOGIN,
                SecurityEventType.ACCOUNT_LOCKED,
                SecurityEventType.NEW_IP_AND_DEVICE_LOGIN
        );

        // Unresolved suspicious events only
        long suspiciousEvents =
                securityEventRepository
                        .countByResolvedFalseAndTypeIn(
                                suspiciousTypes
                        );

        // Total security events in the database
        long totalSecurityEvents =
                securityEventRepository.count();

        // Latest 10 events across the system
        List<SecurityEventDTO> recentEvents =
                securityEventRepository
                        .findTop10ByOrderByCreatedAtDesc()
                        .stream()
                        .map(this::mapToDTO)
                        .toList();

        // Summary for the authenticated admin
        AssignedSecurityEventSummaryDTO assignedSummary =
                securityEventService.getMyAssignedEventSummary();

        // Latest 10 events assigned to the authenticated admin
        List<SecurityEventDTO> assignedEvents =
                securityEventService.getMyAssignedEvents()
                        .stream()
                        .limit(10)
                        .toList();

        // Distribution of event severity
        Map<String, Long> severityDistribution =
                calculateSeverityDistribution();

        // Event creation trend for the last seven calendar days
        List<SecurityTrendPointDTO> recentSecurityTrends =
                calculateRecentTrends();

        // Return complete dashboard response
        return SecurityDashboardDTO.builder()
                .totalUsers(totalUsers)
                .lockedAccounts(lockedAccounts)
                .failedLoginAttempts(failedLoginAttempts)
                .activeSessions(activeSessions)
                .unresolvedSecurityEvents(unresolvedSecurityEvents)
                .suspiciousEvents(suspiciousEvents)
                .totalSecurityEvents(totalSecurityEvents)
                .recentSecurityEvents(recentEvents)
                .assignedEventSummary(assignedSummary)
                .assignedSecurityEvents(assignedEvents)
                .severityDistribution(severityDistribution)
                .recentSecurityTrends(recentSecurityTrends)
                .build();
    }

    /**
     * Groups all security events by the application's severity policy.
     */
    private Map<String, Long> calculateSeverityDistribution() {

        Map<String, Long> distribution = new LinkedHashMap<>();

        distribution.put("CRITICAL", 0L);
        distribution.put("HIGH", 0L);
        distribution.put("MEDIUM", 0L);
        distribution.put("LOW", 0L);

        List<SecurityEvent> events =
                securityEventRepository
                        .findAllByOrderByCreatedAtDesc();

        for (SecurityEvent event : events) {

            SecurityEventType type = event.getType();

            if (type == null) {
                continue;
            }

            switch (type) {

                case ACCOUNT_LOCKED ->
                        distribution.merge(
                                "CRITICAL", 1L, Long::sum
                        );

                case MULTIPLE_FAILED_LOGINS,
                     NEW_IP_AND_DEVICE_LOGIN ->
                        distribution.merge(
                                "HIGH", 1L, Long::sum
                        );

                case NEW_IP_LOGIN,
                     NEW_DEVICE_LOGIN ->
                        distribution.merge(
                                "MEDIUM", 1L, Long::sum
                        );

                case FAILED_LOGIN ->
                        distribution.merge(
                                "LOW", 1L, Long::sum
                        );
            }
        }

        return distribution;
    }

    /**
     * Returns daily security-event counts for the last seven
     * calendar days, including today.
     */
    private List<SecurityTrendPointDTO> calculateRecentTrends() {

        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.minusDays(6);

        LocalDateTime start = firstDay.atStartOfDay();

        List<SecurityEvent> events =
                securityEventRepository
                        .findByCreatedAtAfterOrderByCreatedAtAsc(start);

        Map<LocalDate, Long> dailyCounts = new LinkedHashMap<>();

        // Initialize all seven dates, including dates with no events.
        for (int i = 0; i < 7; i++) {
            dailyCounts.put(firstDay.plusDays(i), 0L);
        }

        // Count events by their creation date.
        for (SecurityEvent event : events) {

            if (event.getCreatedAt() == null) {
                continue;
            }

            LocalDate eventDate =
                    event.getCreatedAt().toLocalDate();

            if (dailyCounts.containsKey(eventDate)) {
                dailyCounts.merge(
                        eventDate, 1L, Long::sum
                );
            }
        }

        List<SecurityTrendPointDTO> trends = new ArrayList<>();

        dailyCounts.forEach((date, count) ->
                trends.add(
                        SecurityTrendPointDTO.builder()
                                .date(date)
                                .eventCount(count)
                                .build()
                )
        );

        return trends;
    }

    /**
     * Maps a SecurityEvent entity to the existing response DTO.
     */
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
