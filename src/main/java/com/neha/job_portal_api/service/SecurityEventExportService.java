
package com.neha.job_portal_api.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.neha.job_portal_api.entity.SecurityEvent;
import com.neha.job_portal_api.repository.SecurityEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityEventExportService {

    private final SecurityEventRepository securityEventRepository;

    public String exportEventsAsCsv() {
        List<SecurityEvent> events =
                securityEventRepository.findAllByOrderByCreatedAtDesc();

        StringBuilder csv = new StringBuilder();

        csv.append("ID,Email,Event Type,Description,IP Address,")
           .append("User Agent,Created At,Resolved\n");

        DateTimeFormatter formatter =
                DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        for (SecurityEvent event : events) {
            csv.append(event.getId()).append(",");
            csv.append(escape(event.getEmail())).append(",");
            csv.append(escape(event.getType() == null
                    ? "" : event.getType().name())).append(",");
            csv.append(escape(event.getDescription())).append(",");
            csv.append(escape(event.getIpAddress())).append(",");
            csv.append(escape(event.getUserAgent())).append(",");
            csv.append(escape(event.getCreatedAt() == null
                    ? "" : event.getCreatedAt().format(formatter))).append(",");
            csv.append(event.isResolved()).append("\n");
        }

        return csv.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "\"\"";
        }

        String safeValue = value.replace("\"", "\"\"");

        return "\"" + safeValue + "\"";
    }
}
