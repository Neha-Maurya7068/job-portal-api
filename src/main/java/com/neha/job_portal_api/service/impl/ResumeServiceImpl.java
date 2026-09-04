package com.neha.job_portal_api.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neha.job_portal_api.dto.ResumeResponseDTO;
import com.neha.job_portal_api.entity.Resume;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.ResumeRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.ResumeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    private final Path uploadDirectory =
            Paths.get("uploads/resumes");

    @Override
    public ResumeResponseDTO uploadResume(MultipartFile file) {

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please select a resume file");
        }

        // Only PDF allowed
        String contentType = file.getContentType();

        if (!"application/pdf".equalsIgnoreCase(contentType)) {
            throw new RuntimeException("Only PDF resume is allowed");
        }

        // Logged-in user's email
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        try {

            // Create directory if not exists
            Files.createDirectories(uploadDirectory);

            // Check existing resume
            Resume existingResume = resumeRepository
                    .findByUserEmail(email)
                    .orElse(null);

            // Delete old physical file
            if (existingResume != null) {

                Path oldFilePath =
                        Paths.get(existingResume.getFilePath());

                Files.deleteIfExists(oldFilePath);

                resumeRepository.delete(existingResume);
            }

            // Generate unique file name
            String originalFileName = file.getOriginalFilename();

            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + originalFileName;

            Path targetPath =
                    uploadDirectory.resolve(fileName);

            // Save file
            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Create Resume entity
            Resume resume = new Resume();

            resume.setFileName(originalFileName);
            resume.setFileType(contentType);
            resume.setFilePath(targetPath.toString());
            resume.setUploadedAt(LocalDateTime.now());
            resume.setUser(user);

            Resume savedResume =
                    resumeRepository.save(resume);

            return convertToDTO(savedResume);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to upload resume",
                    e
            );
        }
    }

    @Override
    public ResumeResponseDTO getMyResume() {

        String email = getLoggedInEmail();

        Resume resume = resumeRepository
                .findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        return convertToDTO(resume);
    }

    @Override
    public Resource downloadResume() {

        String email = getLoggedInEmail();

        Resume resume = resumeRepository
                .findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        try {

            Path filePath =
                    Paths.get(resume.getFilePath());

            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );

            if (!resource.exists()
                    || !resource.isReadable()) {

                throw new RuntimeException(
                        "Resume file not found"
                );
            }

            return resource;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to download resume",
                    e
            );
        }
    }

    @Override
    public void deleteResume() {

        String email = getLoggedInEmail();

        Resume resume = resumeRepository
                .findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        try {

            Path filePath =
                    Paths.get(resume.getFilePath());

            Files.deleteIfExists(filePath);

            resumeRepository.delete(resume);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to delete resume",
                    e
            );
        }
    }

    private String getLoggedInEmail() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    private ResumeResponseDTO convertToDTO(
            Resume resume) {

        return new ResumeResponseDTO(
                resume.getId(),
                resume.getFileName(),
                resume.getFileType(),
                resume.getUploadedAt()
        );
    }
}