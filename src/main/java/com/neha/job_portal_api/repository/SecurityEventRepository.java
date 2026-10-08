package com.neha.job_portal_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.SecurityEvent;
import com.neha.job_portal_api.entity.SecurityEventType;

public interface SecurityEventRepository
        extends JpaRepository<SecurityEvent, Long> {

    List<SecurityEvent> findAllByOrderByCreatedAtDesc();

    List<SecurityEvent> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    boolean existsByEmailAndTypeAndCreatedAtAfter(
            String email,
            SecurityEventType type,
            LocalDateTime after
    );

    long countByResolvedFalse();

    List<SecurityEvent> findTop10ByOrderByCreatedAtDesc();
}