package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.InterviewRequestDTO;
import com.neha.job_portal_api.dto.InterviewResponseDTO;
import com.neha.job_portal_api.entity.InterviewStatus;
import com.neha.job_portal_api.service.InterviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/application/{applicationId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public InterviewResponseDTO scheduleInterview(
            @PathVariable Long applicationId,
            @RequestBody InterviewRequestDTO request) {

        return interviewService.scheduleInterview(
                applicationId,
                request);
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<InterviewResponseDTO>
    getApplicationInterviews(
            @PathVariable Long applicationId) {

        return interviewService
                .getApplicationInterviews(applicationId);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<InterviewResponseDTO> getMyInterviews() {

        return interviewService.getMyInterviews();
    }

    @PutMapping("/{interviewId}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public String updateInterviewStatus(
            @PathVariable Long interviewId,
            @RequestParam InterviewStatus status) {

        interviewService.updateInterviewStatus(
                interviewId,
                status);

        return "Interview status updated successfully";
    }

    @DeleteMapping("/{interviewId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public String deleteInterview(
            @PathVariable Long interviewId) {

        interviewService.deleteInterview(interviewId);

        return "Interview deleted successfully";
    }
}