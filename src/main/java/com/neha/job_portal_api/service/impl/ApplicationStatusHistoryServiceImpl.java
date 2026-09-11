package com.neha.job_portal_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.ApplicationStatusHistoryDTO;
import com.neha.job_portal_api.entity.ApplicationStatusHistory;
import com.neha.job_portal_api.repository.ApplicationStatusHistoryRepository;
import com.neha.job_portal_api.service.ApplicationStatusHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationStatusHistoryServiceImpl
        implements ApplicationStatusHistoryService {

    private final ApplicationStatusHistoryRepository historyRepository;

    @Override
    public List<ApplicationStatusHistoryDTO> getApplicationStatusHistory(
            Long applicationId) {

        List<ApplicationStatusHistory> history =
                historyRepository
                        .findByApplicationIdOrderByChangedAtAsc(
                                applicationId);

        return history.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private ApplicationStatusHistoryDTO mapToDTO(
            ApplicationStatusHistory history) {

        return new ApplicationStatusHistoryDTO(
                history.getId(),
                history.getStatus(),
                history.getChangedAt());
    }
}