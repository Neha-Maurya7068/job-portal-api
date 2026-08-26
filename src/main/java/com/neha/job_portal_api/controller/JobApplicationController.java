package com.neha.job_portal_api.controller;

import java.util.List;
import java.util.Map;

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
    
    @GetMapping("/{applicationId}")
    public JobApplicationResponseDTO getApplicationById(
            @PathVariable Long applicationId) {

        return jobApplicationService.getApplicationById(applicationId);
    }
    
    @GetMapping("/status-count")
    public Map<ApplicationStatus, Long> getApplicationStatusCounts() {

        return jobApplicationService.getApplicationStatusCounts();
    }
    
    @GetMapping("/status")
    public List<JobApplicationResponseDTO> getApplicationsByStatus(
            @RequestParam ApplicationStatus status) {

        return jobApplicationService.getApplicationsByStatus(status);
    }
    
    @GetMapping("/recent")
    public List<JobApplicationResponseDTO> getRecentApplications() {

        return jobApplicationService.getRecentApplications();
    }
    
    @GetMapping("/job/{jobId}/count")
    public long getApplicationCountByJob(
            @PathVariable Long jobId) {

        return jobApplicationService.getApplicationCountByJob(jobId);
    }
    
    @GetMapping("/job/{jobId}")
    public List<JobApplicationResponseDTO> getApplicationsByJob(
            @PathVariable Long jobId) {

        return jobApplicationService.getApplicationsByJob(jobId);
    }
    
   
}