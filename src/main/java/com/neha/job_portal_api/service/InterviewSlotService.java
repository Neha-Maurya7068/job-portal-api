package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.InterviewSlotRequestDTO;
import com.neha.job_portal_api.dto.InterviewSlotResponseDTO;

public interface InterviewSlotService {

    InterviewSlotResponseDTO createSlot(
            InterviewSlotRequestDTO request);

    List<InterviewSlotResponseDTO> getMySlots();

    List<InterviewSlotResponseDTO> getAvailableSlots();

    void deleteSlot(Long slotId);
}