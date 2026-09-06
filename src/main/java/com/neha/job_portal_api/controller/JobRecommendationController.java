package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.dto.ApiResponse;
import com.neha.job_portal_api.dto.JobResponseDTO;
import com.neha.job_portal_api.service.JobRecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobRecommendationController {

    private final JobRecommendationService
            jobRecommendationService;

    @GetMapping("/recommendations")
    public ApiResponse<List<JobResponseDTO>>
            getRecommendedJobs() {

        List<JobResponseDTO> jobs =
                jobRecommendationService
                        .getRecommendedJobs();

        return new ApiResponse<>(
                true,
                "Recommended jobs fetched successfully",
                jobs
        );
    }
}