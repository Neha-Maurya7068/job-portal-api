package com.neha.job_portal_api.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neha.job_portal_api.dto.ApiResponse;
import com.neha.job_portal_api.dto.ResumeResponseDTO;
import com.neha.job_portal_api.service.ResumeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    // Upload Resume
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<ResumeResponseDTO> uploadResume(
            @RequestParam("file") MultipartFile file) {

        ResumeResponseDTO response =
                resumeService.uploadResume(file);

        return new ApiResponse<>(
                true,
                "Resume uploaded successfully",
                response
        );
    }

    // Get Resume Details
    @GetMapping
    public ApiResponse<ResumeResponseDTO> getMyResume() {

        ResumeResponseDTO response =
                resumeService.getMyResume();

        return new ApiResponse<>(
                true,
                "Resume fetched successfully",
                response
        );
    }

    // Download Resume
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadResume() {

        Resource resource =
                resumeService.downloadResume();

        String fileName =
                resource.getFilename();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition
                                .attachment()
                                .filename(fileName)
                                .build()
                                .toString()
                )
                .body(resource);
    }

    // Delete Resume
    @DeleteMapping
    public ApiResponse<Void> deleteResume() {

        resumeService.deleteResume();

        return new ApiResponse<>(
                true,
                "Resume deleted successfully",
                null
        );
    }
}