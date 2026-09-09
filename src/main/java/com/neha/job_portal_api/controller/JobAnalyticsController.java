package com.neha.job_portal_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.dto.ApiResponse;
import com.neha.job_portal_api.dto.JobAnalyticsDTO;
import com.neha.job_portal_api.service.JobAnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recruiter/analytics")
@RequiredArgsConstructor
public class JobAnalyticsController {

    private final JobAnalyticsService jobAnalyticsService;

    @GetMapping
    public ApiResponse<JobAnalyticsDTO> getRecruiterAnalytics() {

        JobAnalyticsDTO analytics =
                jobAnalyticsService.getRecruiterAnalytics();

        return new ApiResponse<>(
                true,
                "Job analytics fetched successfully",
                analytics
        );
    }
}