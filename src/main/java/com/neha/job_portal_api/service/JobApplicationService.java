package com.neha.job_portal_api.service;

import com.neha.job_portal_api.dto.JobApplicationDTO;

public interface JobApplicationService {

    void applyForJob(JobApplicationDTO request);
}