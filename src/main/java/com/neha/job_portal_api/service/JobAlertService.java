package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.JobAlertRequestDTO;
import com.neha.job_portal_api.dto.JobAlertResponseDTO;
import com.neha.job_portal_api.entity.Job;

public interface JobAlertService {

    JobAlertResponseDTO createAlert(JobAlertRequestDTO request);

    List<JobAlertResponseDTO> getMyAlerts();

    void deactivateAlert(Long alertId);

    void deleteAlert(Long alertId);
    
    void processJobAlert(Job job);
}