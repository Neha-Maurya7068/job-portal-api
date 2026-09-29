package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class InterviewSlotRequestDTO {

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}