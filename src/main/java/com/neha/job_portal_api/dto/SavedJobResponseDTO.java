package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobResponseDTO {

    private Long id;

    private Long jobId;

    private String title;

    private String companyName;

    private String location;

    private Double salary;

    private String jobType;

    private Integer experience;

    private LocalDateTime savedAt;
}