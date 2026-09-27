package com.neha.job_portal_api.dto;

import com.neha.job_portal_api.entity.FeedbackRecommendation;

import lombok.Data;

@Data
public class InterviewFeedbackRequestDTO {

    private Integer rating;

    private String technicalSkills;

    private String strengths;

    private String weaknesses;

    private String comments;

    private FeedbackRecommendation recommendation;
}