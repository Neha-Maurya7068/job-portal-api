package com.neha.job_portal_api.dto;

import lombok.Data;

@Data
public class JobSearchRequestDTO {

    private String title;

    private String location;

    private String companyName;

    private Double minSalary;

    private Double maxSalary;

    private Integer minExperience;

    private Integer maxExperience;

    private String jobType;
}