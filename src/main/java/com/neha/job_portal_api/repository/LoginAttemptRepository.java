package com.neha.job_portal_api.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.LoginAttempt;

public interface LoginAttemptRepository
        extends JpaRepository<LoginAttempt, Long> {

    long countByEmailAndSuccessFalseAndAttemptedAtAfter(
            String email,
            LocalDateTime after
    );
}