package com.neha.job_portal_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.JobAlert;

public interface JobAlertRepository
        extends JpaRepository<JobAlert, Long> {

    List<JobAlert> findByUserId(Long userId);

    List<JobAlert> findByActiveTrue();
}