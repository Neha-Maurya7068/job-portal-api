package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.dto.SecurityEventActionDTO;
import com.neha.job_portal_api.entity.SecurityEventActionType;
import com.neha.job_portal_api.service.SecurityEventActionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/security-events")
@RequiredArgsConstructor
public class SecurityEventActionController {

    private final SecurityEventActionService
            securityEventActionService;

    @GetMapping("/{eventId}/history")
    public ResponseEntity<List<SecurityEventActionDTO>>
    getHistory(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                securityEventActionService
                        .getHistory(eventId)
        );
    }

    @PostMapping("/{eventId}/actions")
    public ResponseEntity<SecurityEventActionDTO>
    addAction(
            @PathVariable Long eventId,
            @RequestParam SecurityEventActionType action,
            @RequestParam(required = false) String note) {

        return ResponseEntity.ok(
                securityEventActionService.addAction(
                        eventId,
                        action,
                        note
                )
        );
    }
}