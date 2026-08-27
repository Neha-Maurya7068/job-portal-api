package com.neha.job_portal_api.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.JobRequestDTO;
import com.neha.job_portal_api.dto.JobResponseDTO;
import com.neha.job_portal_api.entity.Job;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.JobApplicationRepository;
import com.neha.job_portal_api.repository.JobRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.JobService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;


    // ================= SEARCH BY SALARY =================

    @Override
    public List<Job> searchJobsBySalary(Double salary) {

        return jobRepository.findBySalaryGreaterThanEqual(salary);
    }


    // ================= SEARCH BY COMPANY =================

    @Override
    public List<Job> searchJobsByCompanyName(String companyName) {

        return jobRepository
                .findByCompanyNameContainingIgnoreCase(companyName);
    }


    // ================= SEARCH BY TITLE + LOCATION =================

    @Override
    public List<Job> searchJobsByTitleAndLocation(
            String title,
            String location) {

        return jobRepository
                .findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(
                        title,
                        location
                );
    }


    // ================= SEARCH BY SALARY + EXPERIENCE =================

    @Override
    public List<Job> searchJobsBySalaryAndExperience(
            Double salary,
            Integer experience) {

        return jobRepository
                .findBySalaryGreaterThanEqualAndExperienceLessThanEqual(
                        salary,
                        experience
                );
    }


    // ================= SEARCH BY EXPERIENCE =================

    @Override
    public List<Job> searchJobsByExperience(Integer experience) {

        return jobRepository
                .findByExperienceLessThanEqual(experience);
    }


    // ================= SEARCH BY JOB TYPE =================

    @Override
    public List<Job> searchJobsByType(String jobType) {

        return jobRepository
                .findByJobTypeContainingIgnoreCase(jobType);
    }


    // ================= SEARCH BY LOCATION =================

    @Override
    public List<Job> searchJobsByLocation(String location) {

        return jobRepository
                .findByLocationContainingIgnoreCase(location);
    }


    // ================= SEARCH BY TITLE =================

    @Override
    public List<Job> searchJobsByTitle(String title) {

        return jobRepository
                .findByTitleContainingIgnoreCase(title);
    }


    // ================= CREATE JOB =================

    @Override
    public JobResponseDTO createJob(JobRequestDTO request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Job job = new Job();

        job.setRecruiter(recruiter);
        job.setTitle(request.getTitle());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setDescription(request.getDescription());
        job.setJobType(request.getJobType());
        job.setExperience(request.getExperience());

        Job savedJob = jobRepository.save(job);

        return convertToDTO(savedJob, 0L);
    }


    // ================= GET ALL JOBS =================

    @Override
    public List<JobResponseDTO> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(job ->
                        convertToDTO(
                                job,
                                jobApplicationRepository
                                        .countByJobId(job.getId())
                        )
                )
                .toList();
    }


    // ================= GET JOB BY ID =================

    @Override
    public JobResponseDTO getJobById(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        long applicationCount =
                jobApplicationRepository.countByJobId(job.getId());

        return convertToDTO(job, applicationCount);
    }


    // ================= GET MY JOBS =================

    @Override
    public List<JobResponseDTO> getMyJobs() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        return jobRepository
                .findByRecruiterId(recruiter.getId())
                .stream()
                .map(job ->
                        convertToDTO(
                                job,
                                jobApplicationRepository
                                        .countByJobId(job.getId())
                        )
                )
                .toList();
    }


    // ================= UPDATE JOB =================

    @Override
    public JobResponseDTO updateJob(
            Long id,
            JobRequestDTO request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Job job = jobRepository
                .findByIdAndRecruiterId(id, recruiter.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job not found or you are not the owner"
                        ));

        job.setTitle(request.getTitle());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setDescription(request.getDescription());
        job.setJobType(request.getJobType());
        job.setExperience(request.getExperience());

        Job updatedJob = jobRepository.save(job);

        long applicationCount =
                jobApplicationRepository.countByJobId(updatedJob.getId());

        return convertToDTO(updatedJob, applicationCount);
    }

    // ================= DELETE JOB =================

    @Override
    public void deleteJob(Long id) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Job job = jobRepository
                .findByIdAndRecruiterId(id, recruiter.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job not found or you are not the owner"
                        ));

        // Delete applications first
        jobApplicationRepository.deleteByJobId(job.getId());

        // Then delete job
        jobRepository.delete(job);
    }
    // ================= CONVERT ENTITY TO DTO =================

    private JobResponseDTO convertToDTO(
            Job job,
            Long applicationCount) {

        return new JobResponseDTO(
                job.getId(),
                job.getTitle(),
                job.getCompanyName(),
                job.getLocation(),
                job.getSalary(),
                job.getDescription(),
                job.getJobType(),
                job.getExperience(),
                job.getCreatedAt(),
                applicationCount
        );
    }
}