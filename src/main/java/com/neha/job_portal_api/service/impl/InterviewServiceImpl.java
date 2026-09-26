package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.InterviewRequestDTO;
import com.neha.job_portal_api.dto.InterviewResponseDTO;
import com.neha.job_portal_api.entity.Interview;
import com.neha.job_portal_api.entity.InterviewStatus;
import com.neha.job_portal_api.entity.JobApplication;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.exception.ResourceNotFoundException;
import com.neha.job_portal_api.repository.InterviewRepository;
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

    @Override
    public InterviewResponseDTO scheduleInterview(
            Long applicationId,
            InterviewRequestDTO request) {

        User recruiter = getCurrentUser();
        User candidate = application.getUser();

        JobApplication application =
                applicationRepository
                        .findByIdAndJobRecruiterId(
                                applicationId,
                                recruiter.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found"));

        if (application.getStatus() !=
                com.neha.job_portal_api.entity.ApplicationStatus.SHORTLISTED) {

            throw new RuntimeException(
                    "Interview can be scheduled only for shortlisted applications");
        }

        Interview interview = new Interview();

        interview.setInterviewDateTime(
                request.getInterviewDateTime());

        interview.setMode(request.getMode());

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

        Interview saved =
                interviewRepository.save(interview);

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
                    "You can update only your own interviews");
        }

        interview.setStatus(status);

        interviewRepository.save(interview);
        
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
                status.name()
        );
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
    public InterviewResponseDTO rescheduleInterview(
            Long interviewId,
            InterviewRequestDTO request) {

        User recruiter = getCurrentUser();

        Interview interview =
                interviewRepository
                        .findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found"));

        // Ownership check
        if (!interview.getCreatedBy()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can reschedule only your own interviews");
        }

        // Cancelled interview cannot be rescheduled
        if (interview.getStatus()
                == InterviewStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled interview cannot be rescheduled");
        }

        interview.setInterviewDateTime(
                request.getInterviewDateTime());

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

        // Candidate email
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
}