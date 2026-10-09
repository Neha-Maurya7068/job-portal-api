package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.SecurityEventActionDTO;
import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.entity.SecurityEventActionType;

public interface SecurityEventActionService {

    List<SecurityEventActionDTO>
    getHistory(Long securityEventId);

    SecurityEventActionDTO addAction(
            Long securityEventId,
            SecurityEventActionType action,
            String note
    );
    SecurityEventDTO resolveEvent(Long id);
    
    List<SecurityEventActionDTO> getInvestigationNotes(Long eventId);

    SecurityEventActionDTO addInvestigationNote(
            Long eventId,
            String note
    );
}