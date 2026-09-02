package com.neha.job_portal_api.service.impl;

import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.AdminDashboardDTO;
import com.neha.job_portal_api.entity.Role;
import com.neha.job_portal_api.repository.JobApplicationRepository;
import com.neha.job_portal_api.repository.JobRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public AdminDashboardDTO getDashboard() {

        long totalUsers = userRepository.count();

        long totalRecruiters = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.RECRUITER)
                .count();

        long totalJobSeekers = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.JOB_SEEKER)
                .count();

        long totalJobs = jobRepository.count();

        long totalApplications = jobApplicationRepository.count();

        return new AdminDashboardDTO(
                totalUsers,
                totalRecruiters,
                totalJobSeekers,
                totalJobs,
                totalApplications
        );
    }
}

