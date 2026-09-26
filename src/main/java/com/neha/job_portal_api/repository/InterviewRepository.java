package com.neha.job_portal_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.Interview;

public interface InterviewRepository
extends JpaRepository<Interview, Long> {

List<Interview>
findByApplicationIdOrderByInterviewDateTimeAsc(
    Long applicationId);

List<Interview>
findByCreatedByIdOrderByInterviewDateTimeAsc(
    Long recruiterId);

List<Interview>
findByApplicationUserIdOrderByInterviewDateTimeAsc(
    Long userId);
}