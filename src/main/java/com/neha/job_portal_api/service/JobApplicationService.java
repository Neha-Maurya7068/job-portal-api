package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.JobApplicationDTO;
import com.neha.job_portal_api.dto.JobApplicationResponseDTO;

public interface JobApplicationService {

    void applyForJob(JobApplicationDTO request);

    List<JobApplicationResponseDTO> getMyApplications();

    List<JobApplicationResponseDTO> getAllApplications();
}