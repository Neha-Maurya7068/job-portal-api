package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.service.SecurityEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/security-events")
@RequiredArgsConstructor
public class SecurityEventController {

    private final SecurityEventService securityEventService;

    @GetMapping
    public ResponseEntity<List<SecurityEventDTO>> getAllEvents() {

        return ResponseEntity.ok(
                securityEventService.getAllEvents()
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<SecurityEventDTO>> getMyEvents() {

        return ResponseEntity.ok(
                securityEventService.getMyEvents()
        );
    }
}