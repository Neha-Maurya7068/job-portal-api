package com.neha.job_portal_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InvestigationNoteRequestDTO {

    @NotBlank(message = "Investigation note is required")
    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    private String note;
}