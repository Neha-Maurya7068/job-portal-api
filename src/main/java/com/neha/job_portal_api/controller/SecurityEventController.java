package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.dto.InvestigationNoteRequestDTO;
import com.neha.job_portal_api.dto.SecurityEventActionDTO;
import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.service.SecurityEventActionService;
import com.neha.job_portal_api.service.SecurityEventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/security-events")
@RequiredArgsConstructor
public class SecurityEventController {

    private final SecurityEventService securityEventService;
    private final SecurityEventActionService securityEventActionService;

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
    
}