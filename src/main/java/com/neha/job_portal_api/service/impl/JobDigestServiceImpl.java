package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.entity.Job;
import com.neha.job_portal_api.entity.JobAlert;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.JobAlertRepository;
import com.neha.job_portal_api.repository.JobRepository;
import com.neha.job_portal_api.service.EmailService;
import com.neha.job_portal_api.service.JobDigestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobDigestServiceImpl implements JobDigestService {

    private final JobAlertRepository jobAlertRepository;

    private final JobRepository jobRepository;

    private final EmailService emailService;

    @Override
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyJobDigest() {

        List<JobAlert> alerts =
                jobAlertRepository
                        .findByActiveTrueAndDailyDigestTrue();

        for (JobAlert alert : alerts) {

            User user = alert.getUser();

            List<Job> matchingJobs =
                    jobRepository.findMatchingJobsForAlert(
                            alert.getTitle(),
                            alert.getLocation(),
                            alert.getJobType(),
                            alert.getMinSalary(),
                            alert.getMinExperience(),
                            LocalDateTime.now()
                    );

            if (matchingJobs.isEmpty()) {
                continue;
            }

            emailService.sendDailyJobDigestEmail(
                    user.getEmail(),
                    user.getName(),
                    matchingJobs
            );
        }
    }
}