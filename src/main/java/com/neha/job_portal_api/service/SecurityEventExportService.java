
package com.neha.job_portal_api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.neha.job_portal_api.entity.SecurityEvent;
import com.neha.job_portal_api.entity.SecurityEventType;
import com.neha.job_portal_api.repository.SecurityEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityEventExportService {

    private final SecurityEventRepository securityEventRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Set<String> ALLOWED_SEVERITIES =
            Set.of("CRITICAL", "HIGH", "MEDIUM", "LOW");

    // Export all security events as CSV
    public String exportEventsAsCsv() {

        List<SecurityEvent> events =
                securityEventRepository.findAllByOrderByCreatedAtDesc();

        StringBuilder csv = new StringBuilder();

        csv.append("ID,Email,Event Type,Description,IP Address,")
           .append("User Agent,Created At,Resolved\n");

        for (SecurityEvent event : events) {
            appendCsvRow(csv, event, false);
        }

        return csv.toString();
    }

    // Export security events filtered by date range and severity
    public String exportEventsAsCsv(
            LocalDate startDate,
            LocalDate endDate,
            String severity) {

        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date cannot be after end date"
            );
        }

        String requestedSeverity = null;

        if (severity != null && !severity.isBlank()) {
            requestedSeverity =
                    severity.trim().toUpperCase(Locale.ROOT);

            if (!ALLOWED_SEVERITIES.contains(requestedSeverity)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Severity must be CRITICAL, HIGH, MEDIUM or LOW"
                );
            }
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime endExclusive =
                endDate.plusDays(1).atStartOfDay();

        List<SecurityEvent> events =
                securityEventRepository
                        .findByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
                                start, endExclusive
                        );

        StringBuilder csv = new StringBuilder();

        csv.append("ID,Email,Event Type,Severity,Description,IP Address,")
           .append("User Agent,Created At,Resolved\n");

        for (SecurityEvent event : events) {

            String eventSeverity = getSeverity(event.getType());

            if (requestedSeverity != null
                    && !requestedSeverity.equals(eventSeverity)) {
                continue;
            }

            appendCsvRow(csv, event, true);
        }

        return csv.toString();
    }

    // Append one properly escaped CSV row
    private void appendCsvRow(
            StringBuilder csv,
            SecurityEvent event,
            boolean includeSeverity) {

        csv.append(event.getId()).append(",");
        csv.append(escape(event.getEmail())).append(",");
        csv.append(escape(
                event.getType() == null
                        ? "" : event.getType().name()
        )).append(",");

        if (includeSeverity) {
            csv.append(escape(getSeverity(event.getType())))
               .append(",");
        }

        csv.append(escape(event.getDescription())).append(",");
        csv.append(escape(event.getIpAddress())).append(",");
        csv.append(escape(event.getUserAgent())).append(",");
        csv.append(escape(
                event.getCreatedAt() == null
                        ? "" : event.getCreatedAt().format(FORMATTER)
        )).append(",");
        csv.append(event.isResolved()).append("\n");
    }

    // Escape CSV values safely
    private String escape(String value) {

        if (value == null) {
            return "\"\"";
        }

        String safeValue = value.replace("\"", "\"\"");

        return "\"" + safeValue + "\"";
    }

    // Determine severity from the event type
    private String getSeverity(SecurityEventType type) {

        if (type == null) {
            return "UNKNOWN";
        }

        return switch (type) {
            case ACCOUNT_LOCKED -> "CRITICAL";

            case MULTIPLE_FAILED_LOGINS,
                 NEW_IP_AND_DEVICE_LOGIN -> "HIGH";

            case NEW_IP_LOGIN,
                 NEW_DEVICE_LOGIN -> "MEDIUM";

            case FAILED_LOGIN -> "LOW";
        };
    }
}
