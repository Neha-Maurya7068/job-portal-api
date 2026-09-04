package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponseDTO {

    private Long id;
    private String fileName;
    private String fileType;
    private LocalDateTime uploadedAt;
}