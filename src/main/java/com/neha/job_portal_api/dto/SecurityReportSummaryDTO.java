
package com.neha.job_portal_api.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityReportSummaryDTO {

    private LocalDate startDate;
    private LocalDate endDate;

    private long totalEvents;
    private long resolvedEvents;
    private long unresolvedEvents;

    private long criticalEvents;
    private long highEvents;
    private long mediumEvents;
    private long lowEvents;
}
