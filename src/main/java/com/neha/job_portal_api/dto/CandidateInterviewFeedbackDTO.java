package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import com.neha.job_portal_api.entity.FeedbackRecommendation;

import lombok.Data;

@Data
public class CandidateInterviewFeedbackDTO {

    private Long interviewId;

    private Long applicationId;

    private String jobTitle;

    private LocalDateTime interviewDateTime;

    private Integer rating;

    private FeedbackRecommendation recommendation;

    private String comments;

    private LocalDateTime feedbackDate;
}