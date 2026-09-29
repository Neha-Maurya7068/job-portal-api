package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.CandidateInterviewFeedbackDTO;
import com.neha.job_portal_api.dto.InterviewFeedbackRequestDTO;
import com.neha.job_portal_api.dto.InterviewFeedbackResponseDTO;
import com.neha.job_portal_api.entity.ApplicationStatus;
import com.neha.job_portal_api.entity.ApplicationStatusHistory;
import com.neha.job_portal_api.entity.Interview;
import com.neha.job_portal_api.entity.InterviewFeedback;
import com.neha.job_portal_api.entity.InterviewStatus;
import com.neha.job_portal_api.entity.JobApplication;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.ApplicationStatusHistoryRepository;
import com.neha.job_portal_api.repository.InterviewFeedbackRepository;
import com.neha.job_portal_api.repository.InterviewRepository;
import com.neha.job_portal_api.repository.JobApplicationRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.EmailService;
import com.neha.job_portal_api.service.InterviewFeedbackService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewFeedbackServiceImpl
        implements InterviewFeedbackService {

    private final InterviewRepository interviewRepository;
    private final InterviewFeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final EmailService emailService;

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
    
    @Override
    public void processRecommendation(Long feedbackId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        InterviewFeedback feedback =
                feedbackRepository.findById(feedbackId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Feedback not found"));

        if (!feedback.getCreatedBy()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can process only your own feedback");
        }

        JobApplication application =
                feedback.getInterview()
                        .getApplication();

        ApplicationStatus newStatus;

        switch (feedback.getRecommendation()) {

            case SELECTED:
                newStatus = ApplicationStatus.ACCEPTED;
                break;

            case REJECTED:
                newStatus = ApplicationStatus.REJECTED;
                break;

            case HOLD:
                newStatus = ApplicationStatus.SHORTLISTED;
                break;

            default:
                throw new RuntimeException(
                        "Invalid recommendation");
        }

        application.setStatus(newStatus);
        application.setStatusUpdatedAt(
                LocalDateTime.now());

        jobApplicationRepository.save(application);

        // Status history
        ApplicationStatusHistory history =
                new ApplicationStatusHistory();

        history.setApplication(application);
        history.setStatus(newStatus);
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy(recruiter);

        historyRepository.save(history);

        // Candidate email
        User candidate = application.getUser();

        emailService.sendApplicationStatusEmail(
                candidate.getEmail(),
                candidate.getName(),
                application.getJob().getTitle(),
                newStatus.name()
        );
    }
    
    @Override
    public CandidateInterviewFeedbackDTO getCandidateFeedback(
            Long interviewId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User candidate = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        InterviewFeedback feedback =
                feedbackRepository
                        .findByInterviewId(interviewId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Interview feedback not found"));

        Interview interview =
                feedback.getInterview();

        // Candidate ownership check
        if (!interview.getApplication()
                .getUser()
                .getId()
                .equals(candidate.getId())) {

            throw new RuntimeException(
                    "You can view feedback only for your own interview");
        }

        CandidateInterviewFeedbackDTO dto =
                new CandidateInterviewFeedbackDTO();

        dto.setInterviewId(interview.getId());

        dto.setApplicationId(
                interview.getApplication().getId());

        dto.setJobTitle(
                interview.getApplication()
                        .getJob()
                        .getTitle());

        dto.setInterviewDateTime(
                interview.getInterviewDateTime());

        dto.setRating(
                feedback.getRating());

        dto.setRecommendation(
                feedback.getRecommendation());

        dto.setComments(
                feedback.getComments());

        dto.setFeedbackDate(
                feedback.getCreatedAt());

        return dto;
    }
}