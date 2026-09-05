package com.neha.job_portal_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.SavedJob;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    Optional<SavedJob> findByUserIdAndJobId(Long userId, Long jobId);

    List<SavedJob> findByUserIdOrderBySavedAtDesc(Long userId);

    void deleteByUserIdAndJobId(Long userId, Long jobId);
}