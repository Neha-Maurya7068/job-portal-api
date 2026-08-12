package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.JobApplicationDTO;
import com.neha.job_portal_api.dto.JobApplicationResponseDTO;
import com.neha.job_portal_api.entity.ApplicationStatus;

public interface JobApplicationService {

    void applyForJob(JobApplicationDTO request);

    List<JobApplicationResponseDTO> getMyApplications();

    List<JobApplicationResponseDTO> getAllApplications();
    
    void updateApplicationStatus
    (Long applicationId, ApplicationStatus status);
}