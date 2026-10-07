package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.AuditLogDTO;
import com.neha.job_portal_api.service.AuditLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogDTO>>
            getAllAuditLogs() {

        return ResponseEntity.ok(
                auditLogService.getAllAuditLogs());
    }

    @GetMapping("/my")
    public ResponseEntity<List<AuditLogDTO>>
            getMyAuditLogs() {

        return ResponseEntity.ok(
                auditLogService.getMyAuditLogs());
    }
}