package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import com.neha.job_portal_api.entity.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponseDTO {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private LocalDateTime appliedAt;
    private ApplicationStatus status;
    private LocalDateTime statusUpdatedAt;
}