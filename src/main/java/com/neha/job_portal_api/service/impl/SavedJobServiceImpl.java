package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.SavedJobResponseDTO;
import com.neha.job_portal_api.entity.Job;
import com.neha.job_portal_api.entity.SavedJob;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.JobRepository;
import com.neha.job_portal_api.repository.SavedJobRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.SavedJobService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavedJobServiceImpl implements SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Override
    public SavedJobResponseDTO saveJob(Long jobId) {

        User user = getLoggedInUser();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        boolean alreadySaved =
                savedJobRepository
                        .existsByUserIdAndJobId(
                                user.getId(),
                                jobId
                        );

        if (alreadySaved) {
            throw new RuntimeException(
                    "Job is already saved"
            );
        }

        SavedJob savedJob = new SavedJob();

        savedJob.setUser(user);
        savedJob.setJob(job);
        savedJob.setSavedAt(LocalDateTime.now());

        SavedJob saved =
                savedJobRepository.save(savedJob);

        return convertToDTO(saved);
    }

    @Override
    public List<SavedJobResponseDTO> getMySavedJobs() {

        User user = getLoggedInUser();

        return savedJobRepository
                .findByUserIdOrderBySavedAtDesc(user.getId())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public boolean isJobSaved(Long jobId) {

        User user = getLoggedInUser();

        return savedJobRepository
                .existsByUserIdAndJobId(
                        user.getId(),
                        jobId
                );
    }

    @Override
    public void removeSavedJob(Long jobId) {

        User user = getLoggedInUser();

        boolean exists =
                savedJobRepository
                        .existsByUserIdAndJobId(
                                user.getId(),
                                jobId
                        );

        if (!exists) {
            throw new RuntimeException(
                    "Saved job not found"
            );
        }

        savedJobRepository.deleteByUserIdAndJobId(
                user.getId(),
                jobId
        );
    }

    private User getLoggedInUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));
    }

    private SavedJobResponseDTO convertToDTO(
            SavedJob savedJob) {

        Job job = savedJob.getJob();

        return new SavedJobResponseDTO(
                savedJob.getId(),
                job.getId(),
                job.getTitle(),
                job.getCompanyName(),
                job.getLocation(),
                job.getSalary(),
                job.getJobType(),
                job.getExperience(),
                savedJob.getSavedAt()
        );
    }
}