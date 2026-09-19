package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.JobAlertRequestDTO;
import com.neha.job_portal_api.dto.JobAlertResponseDTO;
import com.neha.job_portal_api.service.JobAlertService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/job-alerts")
@RequiredArgsConstructor
public class JobAlertController {

    private final JobAlertService jobAlertService;

    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public JobAlertResponseDTO createAlert(
            @RequestBody JobAlertRequestDTO request) {

        return jobAlertService.createAlert(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public List<JobAlertResponseDTO> getMyAlerts() {

        return jobAlertService.getMyAlerts();
    }

    @PutMapping("/{alertId}/deactivate")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public String deactivateAlert(
            @PathVariable Long alertId) {

        jobAlertService.deactivateAlert(alertId);

        return "Job alert deactivated successfully";
    }

    @DeleteMapping("/{alertId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public String deleteAlert(
            @PathVariable Long alertId) {

        jobAlertService.deleteAlert(alertId);

        return "Job alert deleted successfully";
    }
}