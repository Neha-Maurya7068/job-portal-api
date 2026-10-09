package com.neha.job_portal_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.SecurityEventAction;
import com.neha.job_portal_api.entity.SecurityEventActionType;

public interface SecurityEventActionRepository
        extends JpaRepository<SecurityEventAction, Long> {

    List<SecurityEventAction>
    findBySecurityEventIdOrderByPerformedAtDesc(
            Long securityEventId
    );
    
    List<SecurityEventAction>
    findBySecurityEventIdAndActionOrderByPerformedAtDesc(
            Long securityEventId,
            SecurityEventActionType action
    );
}