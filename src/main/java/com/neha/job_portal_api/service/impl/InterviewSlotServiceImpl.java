package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.InterviewSlotRequestDTO;
import com.neha.job_portal_api.dto.InterviewSlotResponseDTO;
import com.neha.job_portal_api.entity.InterviewSlot;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.InterviewSlotRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.InterviewSlotService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewSlotServiceImpl
        implements InterviewSlotService {

    private final InterviewSlotRepository slotRepository;
    private final UserRepository userRepository;

    @Override
    public InterviewSlotResponseDTO createSlot(
            InterviewSlotRequestDTO request) {

        User recruiter = getCurrentUser();

        if (request.getStartTime() == null
                || request.getEndTime() == null) {

            throw new RuntimeException(
                    "Start time and end time are required");
        }

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new RuntimeException(
                    "End time must be after start time");
        }

        boolean overlapping =
                slotRepository
                        .existsByRecruiterIdAndStartTimeLessThanAndEndTimeGreaterThan(
                                recruiter.getId(),
                                request.getEndTime(),
                                request.getStartTime());

        if (overlapping) {
            throw new RuntimeException(
                    "Interview slot overlaps with an existing slot");
        }

        InterviewSlot slot = new InterviewSlot();

        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setAvailable(true);
        slot.setCreatedAt(LocalDateTime.now());
        slot.setRecruiter(recruiter);

        return mapToDTO(
                slotRepository.save(slot));
    }

    @Override
    public List<InterviewSlotResponseDTO> getMySlots() {

        User recruiter = getCurrentUser();

        return slotRepository
                .findByRecruiterIdOrderByStartTimeAsc(
                        recruiter.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<InterviewSlotResponseDTO> getAvailableSlots() {

        User recruiter = getCurrentUser();

        return slotRepository
                .findByRecruiterIdAndAvailableTrueOrderByStartTimeAsc(
                        recruiter.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public void deleteSlot(Long slotId) {

        User recruiter = getCurrentUser();

        InterviewSlot slot =
                slotRepository.findById(slotId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Interview slot not found"));

        if (!slot.getRecruiter()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can delete only your own slots");
        }

        slotRepository.delete(slot);
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Recruiter not found"));
    }

    private InterviewSlotResponseDTO mapToDTO(
            InterviewSlot slot) {

        InterviewSlotResponseDTO dto =
                new InterviewSlotResponseDTO();

        dto.setId(slot.getId());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setAvailable(slot.isAvailable());

        return dto;
    }
}