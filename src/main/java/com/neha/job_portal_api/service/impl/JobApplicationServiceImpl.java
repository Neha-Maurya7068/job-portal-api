package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.JobApplicationDTO;
import com.neha.job_portal_api.entity.ApplicationStatus;
import com.neha.job_portal_api.entity.Job;
import com.neha.job_portal_api.entity.JobApplication;
import com.neha.job_portal_api.repository.JobApplicationRepository;
import com.neha.job_portal_api.repository.JobRepository;
import com.neha.job_portal_api.service.JobApplicationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;

    @Override
    public void applyForJob(JobApplicationDTO request) {

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        JobApplication application = new JobApplication();

        application.setJob(job);
        application.setAppliedAt(LocalDateTime.now());
        application.setStatus(ApplicationStatus.PENDING);

        jobApplicationRepository.save(application);
    }
}