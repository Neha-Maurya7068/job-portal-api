package com.neha.job_portal_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.NotificationPreference;

public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference, Long> {

    Optional<NotificationPreference> findByUserId(Long userId);
    
    Optional<NotificationPreference> findByUserEmail(String email);
    
}