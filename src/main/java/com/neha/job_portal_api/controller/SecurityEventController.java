package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.service.SecurityEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/security-events")
@RequiredArgsConstructor
public class SecurityEventController {

    private final SecurityEventService securityEventService;

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
    
}