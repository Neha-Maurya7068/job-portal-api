package com.neha.job_portal_api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    Page<SecurityEvent> findByAssignedToId(
            Long adminId, Pageable pageable);

    Page<SecurityEvent> findByAssignedToIdAndResolved(
            Long adminId, boolean resolved, Pageable pageable);

    @Query("""
        SELECT e FROM SecurityEvent e
        WHERE e.assignedTo.id = :adminId
          AND (:resolved IS NULL OR e.resolved = :resolved)
        ORDER BY
          CASE
            WHEN e.type = com.neha.job_portal_api.entity.SecurityEventType.ACCOUNT_LOCKED THEN 0
            WHEN e.type = com.neha.job_portal_api.entity.SecurityEventType.MULTIPLE_FAILED_LOGINS THEN 1
            WHEN e.type = com.neha.job_portal_api.entity.SecurityEventType.NEW_IP_AND_DEVICE_LOGIN THEN 1
            WHEN e.type = com.neha.job_portal_api.entity.SecurityEventType.NEW_IP_LOGIN THEN 2
            WHEN e.type = com.neha.job_portal_api.entity.SecurityEventType.NEW_DEVICE_LOGIN THEN 2
            WHEN e.type = com.neha.job_portal_api.entity.SecurityEventType.FAILED_LOGIN THEN 3
            ELSE 4
          END ASC,
          e.createdAt DESC
        """)
    Page<SecurityEvent> findAssignedEventsByPriority(
            @Param("adminId") Long adminId,
            @Param("resolved") Boolean resolved,
            Pageable pageable);
}