package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.ApplicationStatusHistoryDTO;

public interface ApplicationStatusHistoryService {

    List<ApplicationStatusHistoryDTO> getApplicationStatusHistory(
            Long applicationId);
}