package com.neha.job_portal_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.SecurityDashboardDTO;
import com.neha.job_portal_api.service.SecurityDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/security")
@RequiredArgsConstructor
public class SecurityDashboardController {

    private final SecurityDashboardService securityDashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<SecurityDashboardDTO> getDashboard() {

        return ResponseEntity.ok(
                securityDashboardService.getDashboard()
        );
    }
}