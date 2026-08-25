package com.neha.job_portal_api.service;

import java.util.List;
import java.util.Map;

import com.neha.job_portal_api.dto.JobApplicationDTO;
import com.neha.job_portal_api.dto.JobApplicationResponseDTO;
import com.neha.job_portal_api.entity.ApplicationStatus;

public interface JobApplicationService {

    void applyForJob(JobApplicationDTO request);

    List<JobApplicationResponseDTO> getMyApplications();

    List<JobApplicationResponseDTO> getAllApplications();
    
    List<JobApplicationResponseDTO> getApplicationsByStatus(
            ApplicationStatus status
            
               );
    
    JobApplicationResponseDTO getApplicationById(Long applicationId);
    
    Map<ApplicationStatus, Long> getApplicationStatusCounts();
    
    List<JobApplicationResponseDTO> getRecentApplications();
    
    long getApplicationCountByJob(Long jobId);   
    
    List<JobApplicationResponseDTO> getApplicationsByJob(Long jobId);
    
    void deleteApplication(Long applicationId);
    
    void updateApplicationStatus
    (Long applicationId, ApplicationStatus status);
    
  
}