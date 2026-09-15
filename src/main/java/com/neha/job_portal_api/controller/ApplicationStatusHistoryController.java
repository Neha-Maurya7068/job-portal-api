package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.dto.ApplicationStatusHistoryDTO;
import com.neha.job_portal_api.service.ApplicationStatusHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationStatusHistoryController {

    private final ApplicationStatusHistoryService historyService;

    @GetMapping("/{applicationId}/status-history")
    @PreAuthorize("hasAnyRole('RECRUITER', 'JOB_SEEKER', 'ADMIN')")
    public List<ApplicationStatusHistoryDTO> getApplicationStatusHistory(
            @PathVariable Long applicationId) {

        return historyService
                .getApplicationStatusHistory(applicationId);
    }
}