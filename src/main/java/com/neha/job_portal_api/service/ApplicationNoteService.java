package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.ApplicationNoteRequestDTO;
import com.neha.job_portal_api.dto.ApplicationNoteResponseDTO;

public interface ApplicationNoteService {

    ApplicationNoteResponseDTO addNote(
            Long applicationId,
            ApplicationNoteRequestDTO request);

    List<ApplicationNoteResponseDTO>
    getApplicationNotes(Long applicationId);

    void deleteNote(Long noteId);
}