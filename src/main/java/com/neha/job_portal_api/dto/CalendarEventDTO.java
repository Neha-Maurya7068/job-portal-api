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
public class CalendarEventDTO {

    private Long id;

    private String type; // INTERVIEW / AVAILABLE_SLOT

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String title;

    private InterviewStatus status;

    private InterviewMode mode;

    private Long applicationId;

    private String jobTitle;

    private Boolean available;
}