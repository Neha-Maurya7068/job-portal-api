package com.neha.job_portal_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.neha.job_portal_api.dto.JobRequestDTO;
import com.neha.job_portal_api.dto.JobResponseDTO;
import com.neha.job_portal_api.entity.Job;

public interface JobService {

    // ================= JOB CRUD =================

    JobResponseDTO createJob(JobRequestDTO request);

    List<JobResponseDTO> getAllJobs();

    JobResponseDTO getJobById(Long id);

    JobResponseDTO updateJob(Long id, JobRequestDTO request);

    List<JobResponseDTO> getMyJobs();

    void deleteJob(Long id);


    // ================= OLD SEARCH METHODS =================

    List<Job> searchJobsByExperience(Integer experience);

    List<Job> searchJobsByCompanyName(String companyName);

    List<Job> searchJobsByTitleAndLocation(
            String title,
            String location
    );

    List<Job> searchJobsBySalaryAndExperience(
            Double salary,
            Integer experience
    );


    // ================= PAGINATION =================

    Page<JobResponseDTO> getJobs(Pageable pageable);

    Page<JobResponseDTO> searchJobsByTitle(
            String title,
            Pageable pageable
    );

    Page<JobResponseDTO> searchJobsByLocation(
            String location,
            Pageable pageable
    );

    Page<JobResponseDTO> searchJobsByType(
            String jobType,
            Pageable pageable
    );

    Page<JobResponseDTO> searchJobsBySalary(
            Double salary,
            Pageable pageable
    );
}

