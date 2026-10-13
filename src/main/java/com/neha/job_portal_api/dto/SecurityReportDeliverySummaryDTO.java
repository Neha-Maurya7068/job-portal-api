
package com.neha.job_portal_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityReportDeliverySummaryDTO {

    private long totalReports;
    private long sentReports;
    private long failedReports;
    private long pendingReports;
}
