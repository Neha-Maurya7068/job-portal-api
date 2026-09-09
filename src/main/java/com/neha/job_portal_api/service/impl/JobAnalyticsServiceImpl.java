package com.neha.job_portal_api.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.JobAnalyticsDTO;
import com.neha.job_portal_api.entity.ApplicationStatus;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.JobApplicationRepository;
import com.neha.job_portal_api.repository.JobRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.JobAnalyticsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobAnalyticsServiceImpl
        implements JobAnalyticsService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public JobAnalyticsDTO getRecruiterAnalytics() {

        // Get logged-in recruiter's email
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // Find recruiter
        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Recruiter not found"
                        )
                );

        Long recruiterId = recruiter.getId();

        // Total jobs posted by recruiter
        long totalJobs = jobRepository
                .findByRecruiterId(recruiterId)
                .size();

        // Total applications received
        long totalApplications =
                jobApplicationRepository
                        .findByJobRecruiterId(recruiterId)
                        .size();

        // Application status counts
        long pendingApplications =
                jobApplicationRepository
                        .countByJobRecruiterIdAndStatus(
                                recruiterId,
                                ApplicationStatus.PENDING
                        );

        long shortlistedApplications =
                jobApplicationRepository
                        .countByJobRecruiterIdAndStatus(
                                recruiterId,
                                ApplicationStatus.SHORTLISTED
                        );

        long acceptedApplications =
                jobApplicationRepository
                        .countByJobRecruiterIdAndStatus(
                                recruiterId,
                                ApplicationStatus.ACCEPTED
                        );

        long rejectedApplications =
                jobApplicationRepository
                        .countByJobRecruiterIdAndStatus(
                                recruiterId,
                                ApplicationStatus.REJECTED
                        );

        return new JobAnalyticsDTO(
                totalJobs,
                totalApplications,
                pendingApplications,
                shortlistedApplications,
                acceptedApplications,
                rejectedApplications
        );
    }
}