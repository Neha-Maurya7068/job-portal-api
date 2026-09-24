package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.ApplicationNoteRequestDTO;
import com.neha.job_portal_api.dto.ApplicationNoteResponseDTO;
import com.neha.job_portal_api.entity.ApplicationNote;
import com.neha.job_portal_api.entity.JobApplication;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.exception.ResourceNotFoundException;
import com.neha.job_portal_api.repository.ApplicationNoteRepository;
import com.neha.job_portal_api.repository.JobApplicationRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.ApplicationNoteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationNoteServiceImpl
        implements ApplicationNoteService {

    private final ApplicationNoteRepository noteRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Override
    public ApplicationNoteResponseDTO addNote(
            Long applicationId,
            ApplicationNoteRequestDTO request) {

        User recruiter = getCurrentUser();

        JobApplication application =
                applicationRepository
                        .findByIdAndJobRecruiterId(
                                applicationId,
                                recruiter.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found"));

        ApplicationNote note = new ApplicationNote();

        note.setNote(request.getNote());
        note.setCreatedAt(LocalDateTime.now());
        note.setApplication(application);
        note.setCreatedBy(recruiter);

        ApplicationNote saved =
                noteRepository.save(note);

        return mapToDTO(saved);
    }

    @Override
    public List<ApplicationNoteResponseDTO>
    getApplicationNotes(Long applicationId) {

        User recruiter = getCurrentUser();

        applicationRepository
                .findByIdAndJobRecruiterId(
                        applicationId,
                        recruiter.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found"));

        return noteRepository
                .findByApplicationIdOrderByCreatedAtDesc(
                        applicationId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public void deleteNote(Long noteId) {

        User recruiter = getCurrentUser();

        ApplicationNote note =
                noteRepository.findById(noteId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Note not found"));

        if (!note.getCreatedBy()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can delete only your own note");
        }

        noteRepository.delete(note);
    }

    private User getCurrentUser() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }

    private ApplicationNoteResponseDTO mapToDTO(
            ApplicationNote note) {

        return new ApplicationNoteResponseDTO(
                note.getId(),
                note.getNote(),
                note.getCreatedAt(),
                note.getCreatedBy().getId(),
                note.getCreatedBy().getName()
        );
    }
}