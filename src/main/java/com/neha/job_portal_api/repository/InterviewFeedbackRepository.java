package com.neha.job_portal_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.InterviewFeedback;

public interface InterviewFeedbackRepository
        extends JpaRepository<InterviewFeedback, Long> {

    Optional<InterviewFeedback> findByInterviewId(Long interviewId);
}