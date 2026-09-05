package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.JobResponseDTO;

public interface JobRecommendationService {

    List<JobResponseDTO> getRecommendedJobs();
}