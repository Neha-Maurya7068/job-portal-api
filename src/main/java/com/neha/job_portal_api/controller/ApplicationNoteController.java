package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.ApplicationNoteRequestDTO;
import com.neha.job_portal_api.dto.ApplicationNoteResponseDTO;
import com.neha.job_portal_api.service.ApplicationNoteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationNoteController {

    private final ApplicationNoteService noteService;

    @PostMapping("/{applicationId}/notes")
    @PreAuthorize("hasRole('RECRUITER')")
    public ApplicationNoteResponseDTO addNote(
            @PathVariable Long applicationId,
            @RequestBody ApplicationNoteRequestDTO request) {

        return noteService.addNote(
                applicationId,
                request);
    }

    @GetMapping("/{applicationId}/notes")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<ApplicationNoteResponseDTO>
    getApplicationNotes(
            @PathVariable Long applicationId) {

        return noteService
                .getApplicationNotes(applicationId);
    }

    @DeleteMapping("/notes/{noteId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public String deleteNote(
            @PathVariable Long noteId) {

        noteService.deleteNote(noteId);

        return "Application note deleted successfully";
    }
}