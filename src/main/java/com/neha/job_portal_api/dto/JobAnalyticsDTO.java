package com.neha.job_portal_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobAnalyticsDTO {

    private long totalJobs;

    private long totalApplications;

    private long pendingApplications;

    private long shortlistedApplications;

    private long acceptedApplications;

    private long rejectedApplications;
}