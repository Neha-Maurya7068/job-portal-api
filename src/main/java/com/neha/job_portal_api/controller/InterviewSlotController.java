package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.InterviewSlotRequestDTO;
import com.neha.job_portal_api.dto.InterviewSlotResponseDTO;
import com.neha.job_portal_api.service.InterviewSlotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/interview-slots")
@RequiredArgsConstructor
public class InterviewSlotController {

    private final InterviewSlotService slotService;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public InterviewSlotResponseDTO createSlot(
            @RequestBody InterviewSlotRequestDTO request) {

        return slotService.createSlot(request);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<InterviewSlotResponseDTO> getMySlots() {

        return slotService.getMySlots();
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<InterviewSlotResponseDTO> getAvailableSlots() {

        return slotService.getAvailableSlots();
    }

    @DeleteMapping("/{slotId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public String deleteSlot(
            @PathVariable Long slotId) {

        slotService.deleteSlot(slotId);

        return "Interview slot deleted successfully";
    }
}