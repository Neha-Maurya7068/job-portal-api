package com.neha.job_portal_api.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecruiterInterviewDashboardDTO {

    private long upcomingInterviews;

    private long completedInterviews;

    private long cancelledInterviews;

    private long pendingFeedback;

    private long selectedCandidates;

    private long rejectedCandidates;

    private List<InterviewResponseDTO> upcomingInterviewList;
}