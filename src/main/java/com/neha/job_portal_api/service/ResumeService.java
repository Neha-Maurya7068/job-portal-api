package com.neha.job_portal_api.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.neha.job_portal_api.dto.ResumeResponseDTO;

public interface ResumeService {

    ResumeResponseDTO uploadResume(MultipartFile file);

    ResumeResponseDTO getMyResume();

    Resource downloadResume();

    void deleteResume();
}