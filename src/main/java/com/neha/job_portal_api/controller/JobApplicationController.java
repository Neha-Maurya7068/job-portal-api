package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.JobApplicationDTO;
import com.neha.job_portal_api.dto.JobApplicationResponseDTO;
import com.neha.job_portal_api.entity.ApplicationStatus;
import com.neha.job_portal_api.entity.JobApplication;
import com.neha.job_portal_api.service.JobApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String applyForJob(@RequestBody JobApplicationDTO request) {

        jobApplicationService.applyForJob(request);

        return "Job applied successfully";
    }
    
    @GetMapping("/my")
    public List<JobApplicationResponseDTO> getMyApplications() {

        return jobApplicationService.getMyApplications();
    }
    
    @GetMapping
    public List<JobApplicationResponseDTO> getAllApplications() {

        return jobApplicationService.getAllApplications();
    }
    
    @PutMapping("/{applicationId}/status")
    public String updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status) {

        jobApplicationService.updateApplicationStatus(
                applicationId,
                status
        );

        return "Application status updated successfully";
    }
    
}