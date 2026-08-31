package com.neha.job_portal_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterDashboardDTO {

    private long totalJobs;
    private long totalApplications;
    private long pendingApplications;
    private long shortlistedApplications;
    private long rejectedApplications;
}