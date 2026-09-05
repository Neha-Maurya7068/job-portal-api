package com.neha.job_portal_api.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.JobResponseDTO;
import com.neha.job_portal_api.entity.Job;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.JobRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.JobRecommendationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobRecommendationServiceImpl
        implements JobRecommendationService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Override
    public List<JobResponseDTO> getRecommendedJobs() {

        User user = getLoggedInUser();

        List<Job> jobs = jobRepository.findAll();

        List<JobWithScore> recommendedJobs = new ArrayList<>();

        for (Job job : jobs) {

            int score = calculateScore(user, job);

            if (score > 0) {
                recommendedJobs.add(
                        new JobWithScore(job, score)
                );
            }
        }

        recommendedJobs.sort(
                Comparator.comparing(
                        JobWithScore::score
                ).reversed()
        );

        return recommendedJobs.stream()
                .map(JobWithScore::job)
                .map(this::convertToDTO)
                .toList();
    }

    private int calculateScore(User user, Job job) {

        int score = 0;

        // Skills match
        if (user.getSkills() != null
                && !user.getSkills().isBlank()
                && job.getTitle() != null) {

            String userSkills =
                    user.getSkills().toLowerCase();

            String jobTitle =
                    job.getTitle().toLowerCase();

            String[] skills =
                    userSkills.split(",");

            for (String skill : skills) {

                String cleanSkill =
                        skill.trim();

                if (!cleanSkill.isEmpty()
                        && jobTitle.contains(cleanSkill)) {

                    score += 5;
                }
            }
        }

        // Location match
        if (user.getLocation() != null
                && job.getLocation() != null
                && user.getLocation()
                        .equalsIgnoreCase(
                                job.getLocation())) {

            score += 3;
        }

        // Experience match
        if (user.getExperience() != null
                && job.getExperience() != null
                && user.getExperience()
                        >= job.getExperience()) {

            score += 2;
        }

        return score;
    }

    private User getLoggedInUser() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));
    }

    private JobResponseDTO convertToDTO(Job job) {

        JobResponseDTO dto =
                new JobResponseDTO();

        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompanyName(job.getCompanyName());
        dto.setLocation(job.getLocation());
        dto.setSalary(job.getSalary());
        dto.setJobType(job.getJobType());
        dto.setExperience(job.getExperience());

        return dto;
    }

    private record JobWithScore(
            Job job,
            int score
    ) {
    }
}