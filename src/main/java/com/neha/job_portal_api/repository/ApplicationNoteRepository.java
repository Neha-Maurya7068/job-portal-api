package com.neha.job_portal_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.ApplicationNote;

public interface ApplicationNoteRepository
        extends JpaRepository<ApplicationNote, Long> {

    List<ApplicationNote>
    findByApplicationIdOrderByCreatedAtDesc(Long applicationId);
}