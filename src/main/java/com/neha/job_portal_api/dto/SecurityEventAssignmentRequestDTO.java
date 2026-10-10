package com.neha.job_portal_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SecurityEventAssignmentRequestDTO {

    @NotNull(message = "Assigned user ID is required")
    private Long assignedToUserId;
}