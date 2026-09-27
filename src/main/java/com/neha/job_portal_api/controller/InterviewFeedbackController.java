package com.neha.job_portal_api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.InterviewFeedbackRequestDTO;
import com.neha.job_portal_api.dto.InterviewFeedbackResponseDTO;
import com.neha.job_portal_api.service.InterviewFeedbackService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/interview-feedback")
@RequiredArgsConstructor
public class InterviewFeedbackController {

    private final InterviewFeedbackService feedbackService;

    @PostMapping("/{interviewId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public InterviewFeedbackResponseDTO addFeedback(
            @PathVariable Long interviewId,
            @RequestBody InterviewFeedbackRequestDTO request) {

        return feedbackService.addFeedback(
                interviewId,
                request);
    }

    @GetMapping("/{interviewId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public InterviewFeedbackResponseDTO getFeedback(
            @PathVariable Long interviewId) {

        return feedbackService.getFeedback(interviewId);
    }
}