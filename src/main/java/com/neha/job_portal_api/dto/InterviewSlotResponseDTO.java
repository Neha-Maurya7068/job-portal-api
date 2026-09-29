package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class InterviewSlotResponseDTO {

    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean available;
}