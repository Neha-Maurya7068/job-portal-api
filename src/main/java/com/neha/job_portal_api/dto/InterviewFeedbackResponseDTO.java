package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import com.neha.job_portal_api.entity.FeedbackRecommendation;

import lombok.Data;

@Data
public class InterviewFeedbackResponseDTO {

    private Long id;

    private Long interviewId;

    private Long applicationId;

    private String jobTitle;

    private Integer rating;

    private String technicalSkills;

    private String strengths;

    private String weaknesses;

    private String comments;

    private FeedbackRecommendation recommendation;

    private LocalDateTime createdAt;

    private Long createdById;

    private String createdByName;
}