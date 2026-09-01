package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.ApiResponse;
import com.neha.job_portal_api.dto.JobRequestDTO;
import com.neha.job_portal_api.dto.JobResponseDTO;
import com.neha.job_portal_api.entity.Job;
import com.neha.job_portal_api.service.JobService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Validated
public class JobController {

    private final JobService jobService;


    // ================= CREATE JOB =================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    
    public ApiResponse<JobResponseDTO> createJob(
            @Valid @RequestBody JobRequestDTO request) {

        JobResponseDTO job = jobService.createJob(request);

        return new ApiResponse<>(
                true,
                "Job created successfully",
                job
        );
    }


    // ================= GET ALL JOBS =================

    @GetMapping
    public List<JobResponseDTO> getAllJobs() {

        return jobService.getAllJobs();
    }


    // ================= GET JOB BY ID =================

    @GetMapping("/{id}")
    public JobResponseDTO getJobById(
            @PathVariable Long id) {

        return jobService.getJobById(id);
    }


    // ================= UPDATE JOB =================

   
    @PutMapping("/{id}")
    public ApiResponse<JobResponseDTO> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequestDTO request) {

        JobResponseDTO job = jobService.updateJob(id, request);

        return new ApiResponse<>(
                true,
                "Job updated successfully",
                job
        );
    }


    // ================= SEARCH BY TITLE =================

    @GetMapping("/search")
    public Page<JobResponseDTO> searchJobs(
            @RequestParam String title,
            Pageable pageable) {

        return jobService.searchJobsByTitle(title, pageable);
    }


    // ================= SEARCH BY JOB TYPE =================

    @GetMapping("/search/type")
    public Page<JobResponseDTO> searchJobsByType(
            @RequestParam String jobType,
            Pageable pageable) {

        return jobService.searchJobsByType(
                jobType,
                pageable
        );
    }


    // ================= SEARCH BY LOCATION =================

    @GetMapping("/search/location")
    public Page<JobResponseDTO> searchJobsByLocation(
            @RequestParam String location,
            Pageable pageable) {

        return jobService.searchJobsByLocation(
                location,
                pageable
        );
    }


    // ================= SEARCH BY SALARY =================

    @GetMapping("/search/salary")
    public Page<JobResponseDTO> searchJobsBySalary(
            @RequestParam Double salary,
            Pageable pageable) {

        return jobService.searchJobsBySalary(
                salary,
                pageable
        );
    }


    // ================= SEARCH BY EXPERIENCE =================

    @GetMapping("/search/experience")
    public List<Job> searchJobsByExperience(
            @RequestParam Integer experience) {

        return jobService.searchJobsByExperience(experience);
    }


    // ================= SEARCH BY COMPANY =================

    @GetMapping("/search/company")
    public List<Job> searchJobsByCompanyName(
            @RequestParam String companyName) {

        return jobService.searchJobsByCompanyName(companyName);
    }


    // ================= SEARCH BY TITLE + LOCATION =================

    @GetMapping("/search/title-location")
    public List<Job> searchJobsByTitleAndLocation(
            @RequestParam String title,
            @RequestParam String location) {

        return jobService.searchJobsByTitleAndLocation(
                title,
                location
        );
    }


    // ================= SEARCH BY SALARY + EXPERIENCE =================

    @GetMapping("/search/salary-experience")
    public List<Job> searchJobsBySalaryAndExperience(
            @RequestParam Double salary,
            @RequestParam Integer experience) {

        return jobService.searchJobsBySalaryAndExperience(
                salary,
                experience
        );
    }


    // ================= GET MY JOBS =================

    @GetMapping("/my")
    public List<JobResponseDTO> getMyJobs() {

        return jobService.getMyJobs();
    }


    // ================= PAGINATED JOBS =================

    @GetMapping("/page")
    public Page<JobResponseDTO> getJobs(
            Pageable pageable) {

        return jobService.getJobs(pageable);
    }


    // ================= DELETE JOB =================

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(@PathVariable Long id) {

        jobService.deleteJob(id);
    }
}

