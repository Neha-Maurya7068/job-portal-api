package com.neha.job_portal_api.dto;

import lombok.Data;

@Data
public class JobAlertRequestDTO {

    private String title;

    private String location;

    private String jobType;

    private Double minSalary;

    private Integer minExperience;
}