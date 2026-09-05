package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.SavedJobResponseDTO;

public interface SavedJobService {

    SavedJobResponseDTO saveJob(Long jobId);

    List<SavedJobResponseDTO> getMySavedJobs();

    boolean isJobSaved(Long jobId);

    void removeSavedJob(Long jobId);
}