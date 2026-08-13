package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponseDTO createJob(@Valid @RequestBody JobRequestDTO request) {

        return jobService.createJob(request);
    }
    @GetMapping
    public List<JobResponseDTO> getAllJobs() {
        return jobService.getAllJobs();
    }
    
    @GetMapping("/{id}")
    public JobResponseDTO getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }
    
    @PutMapping("/{id}")
    public JobResponseDTO updateJob(@PathVariable Long id,
                                    @Valid @RequestBody JobRequestDTO request) {

        return jobService.updateJob(id, request);
    }
    
    @GetMapping("/search")
    public List<Job> searchJobs(@RequestParam String title) {

        return jobService.searchJobsByTitle(title);
    }
    
}