package com.neha.job_portal_api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.SecurityEvent;
import com.neha.job_portal_api.entity.SecurityEventType;

public interface SecurityEventRepository
        extends JpaRepository<SecurityEvent, Long> {

    List<SecurityEvent> findAllByOrderByCreatedAtDesc();

    List<SecurityEvent> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByEmailAndTypeAndCreatedAtAfter(
            String email,
            SecurityEventType type,
            java.time.LocalDateTime after
    );

    long countByResolvedFalse();

    List<SecurityEvent> findTop10ByOrderByCreatedAtDesc();

    // NEW
    List<SecurityEvent> findByResolvedOrderByCreatedAtDesc(
            boolean resolved
    );
    
    List<SecurityEvent> findByAssignedToIdOrderByCreatedAtDesc(Long adminId);
    
    Page<SecurityEvent> findByAssignedToIdOrderByCreatedAtDesc(
            Long adminId, Pageable pageable);

    Page<SecurityEvent> findByAssignedToIdAndResolvedOrderByCreatedAtDesc(
            Long adminId, boolean resolved, Pageable pageable);
}