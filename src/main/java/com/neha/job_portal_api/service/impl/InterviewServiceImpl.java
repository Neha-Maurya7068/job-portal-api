package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.job_portal_api.dto.InterviewRequestDTO;
import com.neha.job_portal_api.dto.InterviewResponseDTO;
import com.neha.job_portal_api.dto.RecruiterInterviewDashboardDTO;
import com.neha.job_portal_api.entity.ApplicationStatus;
import com.neha.job_portal_api.entity.FeedbackRecommendation;
import com.neha.job_portal_api.entity.Interview;
import com.neha.job_portal_api.entity.InterviewSlot;
import com.neha.job_portal_api.entity.InterviewStatus;
import com.neha.job_portal_api.entity.JobApplication;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.exception.ResourceNotFoundException;
import com.neha.job_portal_api.repository.InterviewFeedbackRepository;
import com.neha.job_portal_api.repository.InterviewRepository;
import com.neha.job_portal_api.repository.InterviewSlotRepository;
import com.neha.job_portal_api.repository.JobApplicationRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.EmailService;
import com.neha.job_portal_api.service.InterviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl
        implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final InterviewFeedbackRepository feedbackRepository;
    private final InterviewSlotRepository slotRepository;
    
    @Transactional
    @Override
    public InterviewResponseDTO scheduleInterview(
            Long applicationId,
            InterviewRequestDTO request) {

        User recruiter = getCurrentUser();

        JobApplication application =
                applicationRepository
                        .findByIdAndJobRecruiterId(
                                applicationId,
                                recruiter.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"));

        if (application.getStatus()
                != ApplicationStatus.SHORTLISTED) {

            throw new RuntimeException(
                    "Interview can be scheduled only for shortlisted candidates");
        }

        InterviewSlot slot =
                slotRepository
                        .findByIdAndRecruiterId(
                                request.getSlotId(),
                                recruiter.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Interview slot not found"));

        if (!slot.isAvailable()) {

            throw new RuntimeException(
                    "Interview slot is already booked");
        }

        Interview interview = new Interview();

        interview.setInterviewDateTime(
                slot.getStartTime());

        interview.setMode(
                request.getMode());

        interview.setMeetingLink(
                request.getMeetingLink());

        interview.setLocation(
                request.getLocation());

        interview.setInterviewerName(
                request.getInterviewerName());

        interview.setRemarks(
                request.getRemarks());

        interview.setStatus(
                InterviewStatus.SCHEDULED);

        interview.setCreatedAt(
                LocalDateTime.now());

        interview.setApplication(application);

        interview.setCreatedBy(recruiter);

        interview.setSlot(slot);

        Interview saved =
                interviewRepository.save(interview);

        // Mark slot as booked
        slot.setAvailable(false);
        slotRepository.save(slot);

        User candidate = application.getUser();

        emailService.sendInterviewEmail(
                candidate.getEmail(),
                candidate.getName(),
                application.getJob().getTitle(),
                saved.getInterviewDateTime().toString(),
                saved.getMode().name(),
                saved.getMeetingLink(),
                saved.getLocation(),
                saved.getStatus().name()
        );

        return mapToDTO(saved);
    }

    @Override
    public List<InterviewResponseDTO>
    getApplicationInterviews(Long applicationId) {

        User recruiter = getCurrentUser();

        applicationRepository
                .findByIdAndJobRecruiterId(
                        applicationId,
                        recruiter.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found"));

        return interviewRepository
                .findByApplicationIdOrderByInterviewDateTimeAsc(
                        applicationId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<InterviewResponseDTO> getMyInterviews() {

        User recruiter = getCurrentUser();

        return interviewRepository
                .findByCreatedByIdOrderByInterviewDateTimeAsc(
                        recruiter.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public void updateInterviewStatus(
            Long interviewId,
            InterviewStatus status) {

        User recruiter = getCurrentUser();

        Interview interview = interviewRepository
                .findById(interviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Interview not found"));

        // Ownership check
        if (!interview.getCreatedBy()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can update only your own interviews");
        }

        // Status validation
        if (status == null) {
            throw new IllegalArgumentException(
                    "Interview status cannot be null");
        }

        /*
         * If interview is cancelled,
         * release the previously booked slot.
         */
        if (status == InterviewStatus.CANCELLED) {

            InterviewSlot slot = interview.getSlot();

            if (slot != null) {
                slot.setAvailable(true);
                slotRepository.save(slot);
            }
            
        }

        // Update interview status
        interview.setStatus(status);

        interviewRepository.save(interview);

        // Send status update email to candidate
        User candidate =
                interview.getApplication().getUser();

        emailService.sendInterviewEmail(
                candidate.getEmail(),
                candidate.getName(),
                interview.getApplication()
                        .getJob()
                        .getTitle(),
                interview.getInterviewDateTime().toString(),
                interview.getMode().name(),
                interview.getMeetingLink(),
                interview.getLocation(),
                status.name());
    }

    @Override
    public void deleteInterview(Long interviewId) {

        User recruiter = getCurrentUser();

        Interview interview =
                interviewRepository
                        .findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found"));

        if (!interview.getCreatedBy()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can delete only your own interviews");
        }

        interviewRepository.delete(interview);
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

    @Override
    public List<InterviewResponseDTO>
    getMyCandidateInterviews() {

        User candidate = getCurrentUser();

        return interviewRepository
                .findByApplicationUserIdOrderByInterviewDateTimeAsc(
                        candidate.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private InterviewResponseDTO mapToDTO(
            Interview interview) {

        return new InterviewResponseDTO(
                interview.getId(),
                interview.getApplication().getId(),
                interview.getApplication()
                        .getJob()
                        .getTitle(),
                interview.getInterviewDateTime(),
                interview.getMode(),
                interview.getMeetingLink(),
                interview.getLocation(),
                interview.getStatus(),
                interview.getInterviewerName(),
                interview.getRemarks(),
                interview.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public InterviewResponseDTO rescheduleInterview(
            Long interviewId,
            InterviewRequestDTO request) {

        User recruiter = getCurrentUser();

        Interview interview =
                interviewRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Interview not found"));

        if (!interview.getCreatedBy()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can reschedule only your own interview");
        }

        if (interview.getStatus() == InterviewStatus.COMPLETED) {

            throw new RuntimeException(
                    "Completed interview cannot be rescheduled");
        }

        if (interview.getStatus() == InterviewStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled interview cannot be rescheduled");
        }

        // New slot
        InterviewSlot newSlot =
                slotRepository
                        .findByIdAndRecruiterId(
                                request.getSlotId(),
                                recruiter.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "New interview slot not found"));

        if (!newSlot.isAvailable()) {

            throw new RuntimeException(
                    "New interview slot is already booked");
        }

        // Release old slot
        InterviewSlot oldSlot =
                interview.getSlot();

        if (oldSlot != null) {
            oldSlot.setAvailable(true);
            slotRepository.save(oldSlot);
        }

        // Book new slot
        newSlot.setAvailable(false);
        slotRepository.save(newSlot);

        // Update interview
        interview.setSlot(newSlot);

        interview.setInterviewDateTime(
                newSlot.getStartTime());

        interview.setMode(
                request.getMode());

        interview.setMeetingLink(
                request.getMeetingLink());

        interview.setLocation(
                request.getLocation());

        interview.setInterviewerName(
                request.getInterviewerName());

        interview.setRemarks(
                request.getRemarks());

        interview.setStatus(
                InterviewStatus.RESCHEDULED);

        Interview updated =
                interviewRepository.save(interview);

        // Candidate notification
        User candidate =
                interview.getApplication().getUser();

        emailService.sendInterviewEmail(
                candidate.getEmail(),
                candidate.getName(),
                interview.getApplication()
                        .getJob()
                        .getTitle(),
                updated.getInterviewDateTime().toString(),
                updated.getMode().name(),
                updated.getMeetingLink(),
                updated.getLocation(),
                updated.getStatus().name()
        );

        return mapToDTO(updated);
    }

    @Override
    public RecruiterInterviewDashboardDTO getRecruiterDashboard() {

        User recruiter = getCurrentUser();

        Long recruiterId = recruiter.getId();

        long upcoming =
                interviewRepository
                        .countByCreatedByIdAndStatus(
                                recruiterId,
                                InterviewStatus.SCHEDULED);

        long completed =
                interviewRepository
                        .countByCreatedByIdAndStatus(
                                recruiterId,
                                InterviewStatus.COMPLETED);

        long cancelled =
                interviewRepository
                        .countByCreatedByIdAndStatus(
                                recruiterId,
                                InterviewStatus.CANCELLED);

        List<Interview> completedInterviews =
                interviewRepository
                        .findByCreatedByIdAndStatus(
                                recruiterId,
                                InterviewStatus.COMPLETED);

        long pendingFeedback = completedInterviews
                .stream()
                .filter(interview ->
                        !feedbackRepository
                                .findByInterviewId(interview.getId())
                                .isPresent())
                .count();

        long selectedCandidates =
                completedInterviews
                        .stream()
                        .filter(interview ->
                                feedbackRepository
                                        .findByInterviewId(
                                                interview.getId())
                                        .map(feedback ->
                                                feedback.getRecommendation()
                                                        == FeedbackRecommendation.SELECTED)
                                        .orElse(false))
                        .count();

        long rejectedCandidates =
                completedInterviews
                        .stream()
                        .filter(interview ->
                                feedbackRepository
                                        .findByInterviewId(
                                                interview.getId())
                                        .map(feedback ->
                                                feedback.getRecommendation()
                                                        == FeedbackRecommendation.REJECTED)
                                        .orElse(false))
                        .count();

        List<Interview> upcomingList =
                interviewRepository
                        .findByCreatedByIdAndStatusOrderByInterviewDateTimeAsc(
                                recruiterId,
                                InterviewStatus.SCHEDULED);

        List<InterviewResponseDTO> upcomingDTO =
                upcomingList.stream()
                        .map(this::mapToDTO)
                        .toList();

        RecruiterInterviewDashboardDTO dashboard =
                new RecruiterInterviewDashboardDTO();

        dashboard.setUpcomingInterviews(upcoming);

        dashboard.setCompletedInterviews(completed);

        dashboard.setCancelledInterviews(cancelled);

        dashboard.setPendingFeedback(pendingFeedback);

        dashboard.setSelectedCandidates(
                selectedCandidates);

        dashboard.setRejectedCandidates(
                rejectedCandidates);

        dashboard.setUpcomingInterviewList(
                upcomingDTO);

        return dashboard;
    }
}