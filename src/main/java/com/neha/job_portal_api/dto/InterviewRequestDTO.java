package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import com.neha.job_portal_api.entity.InterviewMode;

import lombok.Data;

@Data
public class InterviewRequestDTO {

    private LocalDateTime interviewDateTime;

    private InterviewMode mode;

    private String meetingLink;

    private String location;

    private String interviewerName;

    private String remarks;
}