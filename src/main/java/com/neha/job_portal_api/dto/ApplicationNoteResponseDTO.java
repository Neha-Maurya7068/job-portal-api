package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicationNoteResponseDTO {

    private Long id;

    private String note;

    private LocalDateTime createdAt;

    private Long createdById;

    private String createdByName;
}