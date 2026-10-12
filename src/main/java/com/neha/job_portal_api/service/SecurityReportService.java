
package com.neha.job_portal_api.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.neha.job_portal_api.dto.SecurityReportSummaryDTO;
import com.neha.job_portal_api.entity.SecurityEvent;
import com.neha.job_portal_api.entity.SecurityEventType;
import com.neha.job_portal_api.repository.SecurityEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityReportService {

    private final SecurityEventRepository securityEventRepository;

    private List<SecurityEvent> getFilteredEvents(
            LocalDate startDate,
            LocalDate endDate,
            String severity) {

        if (startDate == null || endDate == null
                || startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide a valid start date and end date"
            );
        }

        if (severity != null && !severity.isBlank()) {
            severity = severity.trim().toUpperCase(Locale.ROOT);

            if (!Set.of("CRITICAL", "HIGH", "MEDIUM", "LOW")
                    .contains(severity)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid severity"
                );
            }
        } else {
            severity = null;
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<SecurityEvent> events =
                securityEventRepository
                    .findByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
                            start, end
                    );

        final String selectedSeverity = severity;

        return events.stream()
                .filter(event -> selectedSeverity == null
                        || selectedSeverity.equals(
                                getSeverity(event.getType())))
                .toList();
    }

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

    public SecurityReportSummaryDTO getSummary(
            LocalDate startDate,
            LocalDate endDate,
            String severity) {

        List<SecurityEvent> events =
                getFilteredEvents(startDate, endDate, severity);

        return SecurityReportSummaryDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalEvents(events.size())
                .resolvedEvents(events.stream()
                        .filter(SecurityEvent::isResolved).count())
                .unresolvedEvents(events.stream()
                        .filter(event -> !event.isResolved()).count())
                .criticalEvents(countSeverity(events, "CRITICAL"))
                .highEvents(countSeverity(events, "HIGH"))
                .mediumEvents(countSeverity(events, "MEDIUM"))
                .lowEvents(countSeverity(events, "LOW"))
                .build();
    }

    private long countSeverity(
            List<SecurityEvent> events, String severity) {
        return events.stream()
                .filter(event -> severity.equals(
                        getSeverity(event.getType())))
                .count();
    }

    public byte[] generatePdf(
            LocalDate startDate,
            LocalDate endDate,
            String severity) {

        List<SecurityEvent> events =
                getFilteredEvents(startDate, endDate, severity);

        SecurityReportSummaryDTO summary =
                getSummary(startDate, endDate, severity);

        try (ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();

            Font titleFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headingFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            Paragraph title =
                    new Paragraph("Job Portal - Security Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(
                    "Date range: " + startDate + " to " + endDate));
            document.add(new Paragraph(
                    "Severity filter: "
                            + (severity == null || severity.isBlank()
                                    ? "ALL" : severity.toUpperCase(Locale.ROOT))));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Report Summary", headingFont));
            document.add(new Paragraph(
                    "Total events: " + summary.getTotalEvents()));
            document.add(new Paragraph(
                    "Resolved events: " + summary.getResolvedEvents()));
            document.add(new Paragraph(
                    "Unresolved events: " + summary.getUnresolvedEvents()));
            document.add(new Paragraph(
                    "Critical: " + summary.getCriticalEvents()
                    + " | High: " + summary.getHighEvents()
                    + " | Medium: " + summary.getMediumEvents()
                    + " | Low: " + summary.getLowEvents()));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Security Events", headingFont));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[] {1, 2, 2, 3, 2, 1});

            for (String header : List.of(
                    "ID", "Email", "Type", "Description", "Severity", "Resolved")) {
                table.addCell(new Phrase(header, headingFont));
            }

            for (SecurityEvent event : events) {
                table.addCell(String.valueOf(event.getId()));
                table.addCell(safe(event.getEmail()));
                table.addCell(event.getType() == null
                        ? "" : event.getType().name());
                table.addCell(safe(event.getDescription()));
                table.addCell(getSeverity(event.getType()));
                table.addCell(event.isResolved() ? "Yes" : "No");
            }

            document.add(table);
            document.close();

            return output.toByteArray();

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to generate security PDF report", ex);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
