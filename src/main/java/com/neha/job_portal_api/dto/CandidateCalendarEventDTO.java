package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import com.neha.job_portal_api.entity.InterviewMode;
import com.neha.job_portal_api.entity.InterviewStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCalendarEventDTO {

    private Long interviewId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String jobTitle;

    private InterviewStatus status;

    private InterviewMode mode;

    private String meetingLink;

    private String location;

    private String interviewerName;

    private String remarks;
}