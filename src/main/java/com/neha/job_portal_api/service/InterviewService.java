package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.InterviewRequestDTO;
import com.neha.job_portal_api.dto.InterviewResponseDTO;
import com.neha.job_portal_api.entity.InterviewStatus;

public interface InterviewService {

    InterviewResponseDTO scheduleInterview(
            Long applicationId,
            InterviewRequestDTO request);

    List<InterviewResponseDTO> getApplicationInterviews(
            Long applicationId);

    List<InterviewResponseDTO> getMyInterviews();

    List<InterviewResponseDTO> getMyCandidateInterviews();

    void updateInterviewStatus(
            Long interviewId,
            InterviewStatus status);

    void deleteInterview(Long interviewId);
    
    InterviewResponseDTO rescheduleInterview(
            Long interviewId,
            InterviewRequestDTO request);
}