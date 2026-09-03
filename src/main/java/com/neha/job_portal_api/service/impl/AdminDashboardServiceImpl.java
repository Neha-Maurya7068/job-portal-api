package com.neha.job_portal_api.service.impl;

import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.AdminDashboardDTO;
import com.neha.job_portal_api.entity.ApplicationStatus;
import com.neha.job_portal_api.entity.Role;
import com.neha.job_portal_api.repository.JobApplicationRepository;
import com.neha.job_portal_api.repository.JobRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public AdminDashboardDTO getDashboard() {

        // Total users
        long totalUsers = userRepository.count();

        // Total recruiters
        long totalRecruiters =
                userRepository.countByRole(Role.RECRUITER);

        // Total job seekers
        long totalJobSeekers =
                userRepository.countByRole(Role.JOB_SEEKER);

        // Total jobs
        long totalJobs = jobRepository.count();

        // Total applications
        long totalApplications =
                jobApplicationRepository.count();

        // Application status counts
        long pendingApplications =
                jobApplicationRepository.countByStatus(
                        ApplicationStatus.PENDING);

        long shortlistedApplications =
                jobApplicationRepository.countByStatus(
                        ApplicationStatus.SHORTLISTED);

        long acceptedApplications =
                jobApplicationRepository.countByStatus(
                        ApplicationStatus.ACCEPTED);

        long rejectedApplications =
                jobApplicationRepository.countByStatus(
                        ApplicationStatus.REJECTED);

        return new AdminDashboardDTO(
                totalUsers,
                totalRecruiters,
                totalJobSeekers,
                totalJobs,
                totalApplications,
                pendingApplications,
                shortlistedApplications,
                acceptedApplications,
                rejectedApplications
        );
    }
}