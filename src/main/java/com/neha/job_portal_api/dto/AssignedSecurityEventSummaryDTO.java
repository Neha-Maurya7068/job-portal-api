package com.neha.job_portal_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedSecurityEventSummaryDTO {

    private long totalAssigned;
    private long openEvents;
    private long resolvedEvents;

    private long criticalEvents;
    private long highPriorityEvents;
    private long mediumPriorityEvents;
    private long lowPriorityEvents;
}