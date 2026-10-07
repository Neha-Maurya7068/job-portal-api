package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.entity.SecurityEventType;

public interface SecurityEventService {

    void createEvent(
            String email,
            SecurityEventType type,
            String description,
            String ipAddress,
            String userAgent
    );

    List<SecurityEventDTO> getAllEvents();

    List<SecurityEventDTO> getMyEvents();
}