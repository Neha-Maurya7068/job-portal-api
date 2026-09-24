package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import com.neha.job_portal_api.entity.InterviewMode;
import com.neha.job_portal_api.entity.InterviewStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterviewResponseDTO {

    private Long id;

    private Long applicationId;

    private String jobTitle;

    private LocalDateTime interviewDateTime;

    private InterviewMode mode;

    private String meetingLink;

    private String location;

    private InterviewStatus status;

    private String interviewerName;

    private String remarks;

    private LocalDateTime createdAt;
}