package com.neha.job_portal_api.service;

import com.neha.job_portal_api.dto.CandidateInterviewFeedbackDTO;
import com.neha.job_portal_api.dto.InterviewFeedbackRequestDTO;
import com.neha.job_portal_api.dto.InterviewFeedbackResponseDTO;

public interface InterviewFeedbackService {

    InterviewFeedbackResponseDTO addFeedback(
            Long interviewId,
            InterviewFeedbackRequestDTO request);

    InterviewFeedbackResponseDTO getFeedback(
            Long interviewId);
    
    void processRecommendation(Long feedbackId);
    
    CandidateInterviewFeedbackDTO getCandidateFeedback(
            Long interviewId);
}