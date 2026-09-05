package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.ApiResponse;
import com.neha.job_portal_api.dto.SavedJobResponseDTO;
import com.neha.job_portal_api.service.SavedJobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    // Save Job
    @PostMapping("/{jobId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SavedJobResponseDTO> saveJob(
            @PathVariable Long jobId) {

        SavedJobResponseDTO response =
                savedJobService.saveJob(jobId);

        return new ApiResponse<>(
                true,
                "Job saved successfully",
                response
        );
    }

    // Get My Saved Jobs
    @GetMapping
    public ApiResponse<List<SavedJobResponseDTO>>
            getMySavedJobs() {

        List<SavedJobResponseDTO> response =
                savedJobService.getMySavedJobs();

        return new ApiResponse<>(
                true,
                "Saved jobs fetched successfully",
                response
        );
    }

    // Check Job Saved
    @GetMapping("/{jobId}/exists")
    public ApiResponse<Boolean> isJobSaved(
            @PathVariable Long jobId) {

        boolean saved =
                savedJobService.isJobSaved(jobId);

        return new ApiResponse<>(
                true,
                "Saved job status fetched successfully",
                saved
        );
    }

    // Remove Saved Job
    @DeleteMapping("/{jobId}")
    public ApiResponse<Void> removeSavedJob(
            @PathVariable Long jobId) {

        savedJobService.removeSavedJob(jobId);

        return new ApiResponse<>(
                true,
                "Job removed from saved jobs",
                null
        );
    }
}
     


