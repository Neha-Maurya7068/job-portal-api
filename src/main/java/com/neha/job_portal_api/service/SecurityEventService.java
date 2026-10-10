package com.neha.job_portal_api.service;

import java.util.List;

import org.springframework.data.domain.Page;

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

    // NEW
    List<SecurityEventDTO> getEventsByResolved(boolean resolved);

    // NEW
    SecurityEventDTO getEventById(Long id);

    // NEW
    SecurityEventDTO resolveEvent(Long id);
    
    SecurityEventDTO reopenEvent(Long id, String note);
    
    SecurityEventDTO assignEvent(Long eventId, Long assignedToUserId);
    
    List<SecurityEventDTO> getMyAssignedEvents();
    
    Page<SecurityEventDTO> getMyAssignedEvents(
            String status, int page, int size);
    
    Page<SecurityEventDTO> getMyAssignedEvents(
            String status,
            String sortBy,
            String direction,
            int page,
            int size);
}