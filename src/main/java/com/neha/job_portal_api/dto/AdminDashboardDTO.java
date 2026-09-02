package com.neha.job_portal_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {

    private long totalUsers;
    private long totalRecruiters;
    private long totalJobSeekers;
    private long totalJobs;
    private long totalApplications;
}

