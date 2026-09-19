package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobAlertResponseDTO {

    private Long id;

    private String title;

    private String location;

    private String jobType;

    private Double minSalary;

    private Integer minExperience;

    private boolean active;

    private LocalDateTime createdAt;
}