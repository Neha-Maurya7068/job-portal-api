package com.neha.job_portal_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.ApplicationStatus;
import com.neha.job_portal_api.entity.JobApplication;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);
    
    List<JobApplication> findByUserId(Long userId);
    
List<JobApplication> findByJobId(Long jobId);	

List<JobApplication> findByJobRecruiterId(Long recruiterId);

List<JobApplication> findByJobRecruiterIdAndStatus(
        Long recruiterId,
        ApplicationStatus status
);

Optional<JobApplication> findByIdAndJobRecruiterId(
        Long applicationId,
        Long recruiterId
);

long countByJobRecruiterIdAndStatus(
        Long recruiterId,
        ApplicationStatus status
);

}