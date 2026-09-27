package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.InterviewFeedbackRequestDTO;
import com.neha.job_portal_api.dto.InterviewFeedbackResponseDTO;
import com.neha.job_portal_api.entity.Interview;
import com.neha.job_portal_api.entity.InterviewFeedback;
import com.neha.job_portal_api.entity.InterviewStatus;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.InterviewFeedbackRepository;
import com.neha.job_portal_api.repository.InterviewRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.InterviewFeedbackService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewFeedbackServiceImpl
        implements InterviewFeedbackService {

    private final InterviewRepository interviewRepository;
    private final InterviewFeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Override
    public InterviewFeedbackResponseDTO addFeedback(
            Long interviewId,
            InterviewFeedbackRequestDTO request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Interview interview = interviewRepository
                .findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException("Interview not found"));

        // Ownership check
        if (!interview.getCreatedBy()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can add feedback only for your own interview");
        }

        // Feedback only after completed interview
        if (interview.getStatus()
                != InterviewStatus.COMPLETED) {

            throw new RuntimeException(
                    "Feedback can be added only after interview is completed");
        }

        // Prevent duplicate feedback
        if (feedbackRepository
                .findByInterviewId(interviewId)
                .isPresent()) {

            throw new RuntimeException(
                    "Feedback already exists for this interview");
        }

        // Rating validation
        if (request.getRating() == null
                || request.getRating() < 1
                || request.getRating() > 5) {

            throw new RuntimeException(
                    "Rating must be between 1 and 5");
        }

        InterviewFeedback feedback =
                new InterviewFeedback();

        feedback.setRating(request.getRating());
        feedback.setTechnicalSkills(
                request.getTechnicalSkills());
        feedback.setStrengths(
                request.getStrengths());
        feedback.setWeaknesses(
                request.getWeaknesses());
        feedback.setComments(
                request.getComments());
        feedback.setRecommendation(
                request.getRecommendation());

        feedback.setCreatedAt(
                LocalDateTime.now());

        feedback.setInterview(interview);
        feedback.setCreatedBy(recruiter);

        InterviewFeedback saved =
                feedbackRepository.save(feedback);

        return mapToDTO(saved);
    }

    @Override
    public InterviewFeedbackResponseDTO getFeedback(
            Long interviewId) {

        InterviewFeedback feedback =
                feedbackRepository
                        .findByInterviewId(interviewId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Feedback not found"));

        return mapToDTO(feedback);
    }

    private InterviewFeedbackResponseDTO mapToDTO(
            InterviewFeedback feedback) {

        Interview interview =
                feedback.getInterview();

        InterviewFeedbackResponseDTO dto =
                new InterviewFeedbackResponseDTO();

        dto.setId(feedback.getId());
        dto.setInterviewId(interview.getId());

        dto.setApplicationId(
                interview.getApplication().getId());

        dto.setJobTitle(
                interview.getApplication()
                        .getJob()
                        .getTitle());

        dto.setRating(
                feedback.getRating());

        dto.setTechnicalSkills(
                feedback.getTechnicalSkills());

        dto.setStrengths(
                feedback.getStrengths());

        dto.setWeaknesses(
                feedback.getWeaknesses());

        dto.setComments(
                feedback.getComments());

        dto.setRecommendation(
                feedback.getRecommendation());

        dto.setCreatedAt(
                feedback.getCreatedAt());

        dto.setCreatedById(
                feedback.getCreatedBy().getId());

        dto.setCreatedByName(
                feedback.getCreatedBy().getName());

        return dto;
    }
}