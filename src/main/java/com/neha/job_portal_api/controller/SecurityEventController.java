package com.neha.job_portal_api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.neha.job_portal_api.dto.AssignedSecurityEventSummaryDTO;
import com.neha.job_portal_api.dto.InvestigationNoteRequestDTO;
import com.neha.job_portal_api.dto.SecurityEventActionDTO;
import com.neha.job_portal_api.dto.SecurityEventAssignmentRequestDTO;
import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.dto.SecurityReportSummaryDTO;
import com.neha.job_portal_api.service.SecurityEventActionService;
import com.neha.job_portal_api.service.SecurityEventExportService;
import com.neha.job_portal_api.service.SecurityEventService;
import com.neha.job_portal_api.service.SecurityReportEmailService;
import com.neha.job_portal_api.service.SecurityReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/security-events")
@RequiredArgsConstructor
public class SecurityEventController {

    private final SecurityEventService securityEventService;
    private final SecurityEventActionService securityEventActionService;
    private final SecurityEventExportService securityEventExportService;
    private final SecurityReportService securityReportService;
    private final SecurityReportEmailService securityReportEmailService;

    // ADMIN - All events
    @GetMapping
    public ResponseEntity<List<SecurityEventDTO>> getAllEvents(
            @RequestParam(required = false) Boolean resolved) {

        if (resolved != null) {

            return ResponseEntity.ok(
                    securityEventService
                            .getEventsByResolved(resolved)
            );
        }

        return ResponseEntity.ok(
                securityEventService.getAllEvents()
        );
    }

    // ADMIN - Event details
    @GetMapping("/{id}")
    public ResponseEntity<SecurityEventDTO> getEventById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                securityEventService.getEventById(id)
        );
    }

    // ADMIN - Mark event as resolved
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<SecurityEventDTO> resolveEvent(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                securityEventService.resolveEvent(id)
        );
    }

    // Current user's security events
    @GetMapping("/my")
    public ResponseEntity<List<SecurityEventDTO>> getMyEvents() {

        return ResponseEntity.ok(
                securityEventService.getMyEvents()
        );
    }
    
    @PatchMapping("/{id}/reopen")
    public ResponseEntity<SecurityEventDTO> reopenEvent(
            @PathVariable Long id,
            @RequestParam String note) {

        return ResponseEntity.ok(
                securityEventService.reopenEvent(id, note)
        );
    }
    
    @GetMapping("/{eventId}/notes")
    public ResponseEntity<List<SecurityEventActionDTO>> getNotes(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                securityEventActionService.getInvestigationNotes(eventId)
        );
    }

    @PostMapping("/{eventId}/notes")
    public ResponseEntity<SecurityEventActionDTO> addNote(
            @PathVariable Long eventId,
            @Valid @RequestBody InvestigationNoteRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                securityEventActionService.addInvestigationNote(
                        eventId,
                        request.getNote()
                )
        );
    }
    @PostMapping("/{id}/assign")
    public ResponseEntity<SecurityEventDTO> assignEvent(
            @PathVariable Long id,
            @Valid @RequestBody SecurityEventAssignmentRequestDTO request) {

        return ResponseEntity.ok(
                securityEventService.assignEvent(
                        id,
                        request.getAssignedToUserId()
                )
        );
    }
    
    @GetMapping("/assigned-to-me")
    public ResponseEntity<Page<SecurityEventDTO>> getMyAssignedEvents(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "PRIORITY") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                securityEventService.getMyAssignedEvents(
                        status, sortBy, direction, page, size));
    }
    
    @GetMapping("/assigned-to-me/summary")
    public ResponseEntity<AssignedSecurityEventSummaryDTO>
            getMyAssignedEventSummary() {

        return ResponseEntity.ok(
                securityEventService.getMyAssignedEventSummary());
    }
    

@GetMapping(value = "/export/csv", produces = "text/csv")
public ResponseEntity<String> exportSecurityEventsCsv() {
    String csv = securityEventExportService.exportEventsAsCsv();

    return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"security-events.csv\""
            )
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv);
}


@GetMapping(value = "/export/csv/filtered", produces = "text/csv")
public ResponseEntity<String> exportFilteredSecurityEvents(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        @RequestParam(required = false)
        String severity) {

    String csv = securityEventExportService.exportEventsAsCsv(
            startDate, endDate, severity
    );

    return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"security-events-filtered.csv\""
            )
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv);
}

@GetMapping("/report/summary")
public ResponseEntity<SecurityReportSummaryDTO> getSecurityReportSummary(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        @RequestParam(required = false)
        String severity) {

    return ResponseEntity.ok(
            securityReportService.getSummary(startDate, endDate, severity)
    );
}

@GetMapping(value = "/report/pdf", produces = "application/pdf")
public ResponseEntity<byte[]> downloadSecurityReportPdf(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        @RequestParam(required = false)
        String severity) {

    byte[] pdf = securityReportService.generatePdf(
            startDate, endDate, severity
    );

    return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"security-report.pdf\""
            )
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}



@PostMapping("/report/email")
public ResponseEntity<String> emailSecurityReport(
        @RequestParam String recipient,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,
        @RequestParam(required = false) String severity)
        throws jakarta.mail.MessagingException {

    if (recipient == null
            || !recipient.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid recipient email");
    }

    securityReportEmailService.sendPdfReport(
            recipient, startDate, endDate, severity);

    return ResponseEntity.ok("Security report email sent successfully");
}

}