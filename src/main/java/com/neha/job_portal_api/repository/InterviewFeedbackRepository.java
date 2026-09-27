package com.neha.job_portal_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.Interview;
import com.neha.job_portal_api.entity.InterviewFeedback;
import com.neha.job_portal_api.entity.InterviewStatus;

public interface InterviewFeedbackRepository
        extends JpaRepository<InterviewFeedback, Long> {

    Optional<InterviewFeedback> findByInterviewId(Long interviewId);
    
    long countByCreatedById(Long recruiterId);
    
    List<Interview> findByCreatedByIdAndStatus(
            Long recruiterId,
            InterviewStatus status);
}