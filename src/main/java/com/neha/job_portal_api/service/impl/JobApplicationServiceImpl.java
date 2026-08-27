package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.JobApplicationDTO;
import com.neha.job_portal_api.dto.JobApplicationResponseDTO;
import com.neha.job_portal_api.entity.ApplicationStatus;
import com.neha.job_portal_api.entity.Job;
import com.neha.job_portal_api.entity.JobApplication;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.exception.AlreadyAppliedException;
import com.neha.job_portal_api.repository.JobApplicationRepository;
import com.neha.job_portal_api.repository.JobRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.JobApplicationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public void applyForJob(JobApplicationDTO request) {

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyApplied =
                jobApplicationRepository.existsByUserIdAndJobId(
                        user.getId(),
                        job.getId()
                );

        if (alreadyApplied) {
            throw new AlreadyAppliedException(
                    "You have already applied for this job"
            );
        }

        JobApplication application = new JobApplication();

        application.setJob(job);
        application.setUser(user);
        application.setAppliedAt(LocalDateTime.now());
        application.setStatus(ApplicationStatus.PENDING);

        jobApplicationRepository.save(application);
    }

    @Override
    public List<JobApplicationResponseDTO> getMyApplications() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jobApplicationRepository.findByUserId(user.getId())
                .stream()
                .map(application -> new JobApplicationResponseDTO(
                        application.getId(),
                        application.getJob().getId(),
                        application.getJob().getTitle(),
                        application.getJob().getCompanyName(),
                        application.getAppliedAt(),
                        application.getStatus(),
                        application.getStatusUpdatedAt()
                ))
                .toList();
    }

    @Override
    public List<JobApplicationResponseDTO> getAllApplications() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        return jobApplicationRepository
                .findByJobRecruiterId(recruiter.getId())
                .stream()
                .map(application -> new JobApplicationResponseDTO(
                        application.getId(),
                        application.getJob().getId(),
                        application.getJob().getTitle(),
                        application.getJob().getCompanyName(),
                        application.getAppliedAt(),
                        application.getStatus(),
                        application.getStatusUpdatedAt()
                ))
                .toList();
    }
    
    @Override
    public List<JobApplicationResponseDTO> getRecentApplications() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        return jobApplicationRepository
                .findTop5ByJobRecruiterIdOrderByAppliedAtDesc(
                        recruiter.getId()
                )
                .stream()
                .map(application -> new JobApplicationResponseDTO(
                        application.getId(),
                        application.getJob().getId(),
                        application.getJob().getTitle(),
                        application.getJob().getCompanyName(),
                        application.getAppliedAt(),
                        application.getStatus(),
                        application.getStatusUpdatedAt()
                ))
                .toList();
    }
    
    
    @Override
    public List<JobApplicationResponseDTO> getApplicationsByJob(Long jobId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        return jobApplicationRepository
                .findByJobIdAndJobRecruiterId(
                        jobId,
                        recruiter.getId()
                )
                .stream()
                .map(application -> new JobApplicationResponseDTO(
                        application.getId(),
                        application.getJob().getId(),
                        application.getJob().getTitle(),
                        application.getJob().getCompanyName(),
                        application.getAppliedAt(),
                        application.getStatus(),
                        application.getStatusUpdatedAt()
                ))
                .toList();
    }
    
    @Override
    public long getApplicationCountByJob(Long jobId) {

        return jobApplicationRepository.countByJobId(jobId);
    }
    @Override
    public List<JobApplicationResponseDTO> getApplicationsByStatus(
            ApplicationStatus status) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        return jobApplicationRepository
                .findByJobRecruiterIdAndStatus(
                        recruiter.getId(),
                        status
                )
                .stream()
                .map(application -> new JobApplicationResponseDTO(
                        application.getId(),
                        application.getJob().getId(),
                        application.getJob().getTitle(),
                        application.getJob().getCompanyName(),
                        application.getAppliedAt(),
                        application.getStatus(),
                        application.getStatusUpdatedAt()
                ))
                .toList();
    }
    
   

    @Override
    public JobApplicationResponseDTO getApplicationById(
            Long applicationId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        JobApplication application = jobApplicationRepository
                .findByIdAndJobRecruiterId(
                        applicationId,
                        recruiter.getId()
                )
                .orElseThrow(() ->
                        new RuntimeException("Application not found"));

        return new JobApplicationResponseDTO(
                application.getId(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getJob().getCompanyName(),
                application.getAppliedAt(),
                application.getStatus(),
                application.getStatusUpdatedAt()
        );
    }

    @Override
    public Map<ApplicationStatus, Long> getApplicationStatusCounts() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Map<ApplicationStatus, Long> counts =
                new EnumMap<>(ApplicationStatus.class);

        for (ApplicationStatus status : ApplicationStatus.values()) {

            long count = jobApplicationRepository
                    .countByJobRecruiterIdAndStatus(
                            recruiter.getId(),
                            status
                    );

            counts.put(status, count);
        }

        return counts;
    }

    @Override
    public void deleteApplication(Long applicationId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        JobApplication application =
                jobApplicationRepository
                        .findByIdAndJobRecruiterId(
                                applicationId,
                                recruiter.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found or you are not the owner"
                                ));

        jobApplicationRepository.delete(application);
    }
    
    @Override
    public void updateApplicationStatus(
            Long applicationId,
            ApplicationStatus status) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        JobApplication application = jobApplicationRepository
                .findByIdAndJobRecruiterId(
                        applicationId,
                        recruiter.getId()
                )
                .orElseThrow(() ->
                        new RuntimeException("Application not found"));

        application.setStatus(status);

        application.setStatusUpdatedAt(LocalDateTime.now());

        jobApplicationRepository.save(application);
    }
    
  
}