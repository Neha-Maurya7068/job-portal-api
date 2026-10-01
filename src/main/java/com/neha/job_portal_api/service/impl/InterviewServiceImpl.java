package com.neha.job_portal_api.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.job_portal_api.dto.CalendarEventDTO;
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
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final InterviewFeedbackRepository feedbackRepository;
    private final InterviewSlotRepository slotRepository;

    @Override
    @Transactional
    public InterviewResponseDTO scheduleInterview(
            Long applicationId,
            InterviewRequestDTO request) {

        User recruiter = getCurrentUser();

        // 1. Validate application
        JobApplication application =
                applicationRepository
                        .findByIdAndJobRecruiterId(
                                applicationId,
                                recruiter.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found"));

        // 2. Only shortlisted candidates can be scheduled
        if (application.getStatus()
                != ApplicationStatus.SHORTLISTED) {

            throw new RuntimeException(
                    "Interview can be scheduled only for shortlisted candidates");
        }

        // 3. Validate slot ID
        if (request.getSlotId() == null) {
            throw new IllegalArgumentException(
                    "Interview slot is required");
        }

        // 4. Find recruiter-owned slot
        InterviewSlot slot =
                slotRepository
                        .findByIdAndRecruiterId(
                                request.getSlotId(),
                                recruiter.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview slot not found"));

        // 5. Check slot availability
        if (!slot.isAvailable()) {
            throw new RuntimeException(
                    "Interview slot is already booked");
        }

        // 6. Check recruiter interview conflict
        boolean conflict =
                interviewRepository.existsInterviewConflict(
                        recruiter.getId(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        null);

        if (conflict) {
            throw new RuntimeException(
                    "Interview conflicts with another scheduled interview");
        }

        // 7. Create interview
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

        interview.setApplication(
                application);

        interview.setCreatedBy(
                recruiter);

        interview.setSlot(
                slot);

        // 8. Save interview
        Interview saved =
                interviewRepository.save(interview);

        // 9. Mark slot as booked
        slot.setAvailable(false);
        slotRepository.save(slot);

        // 10. Send email to candidate
        User candidate =
                application.getUser();

        emailService.sendInterviewEmail(
                candidate.getEmail(),
                candidate.getName(),
                application.getJob().getTitle(),
                saved.getInterviewDateTime().toString(),
                saved.getMode().name(),
                saved.getMeetingLink(),
                saved.getLocation(),
                saved.getStatus().name());

        // 11. Return response
        return mapToDTO(saved);
    }

    @Override
    public List<InterviewResponseDTO> getApplicationInterviews(
            Long applicationId) {

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
    @Transactional
    public void updateInterviewStatus(
            Long interviewId,
            InterviewStatus status) {

        User recruiter = getCurrentUser();

        // 1. Find interview
        Interview interview =
                interviewRepository
                        .findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found"));

        // 2. Ownership validation
        if (interview.getCreatedBy() == null
                || !interview.getCreatedBy()
                        .getId()
                        .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can update only your own interviews");
        }

        // 3. Status validation
        if (status == null) {
            throw new IllegalArgumentException(
                    "Interview status cannot be null");
        }

        // 4. Release slot when interview is cancelled
        if (status == InterviewStatus.CANCELLED) {

            InterviewSlot slot =
                    interview.getSlot();

            if (slot != null && !slot.isAvailable()) {

                slot.setAvailable(true);
                slotRepository.save(slot);
            }
        }

        // 5. Update interview status
        interview.setStatus(status);

        Interview saved =
                interviewRepository.save(interview);

        // 6. Notify candidate
        User candidate =
                saved.getApplication().getUser();

        emailService.sendInterviewEmail(
                candidate.getEmail(),
                candidate.getName(),
                saved.getApplication()
                        .getJob()
                        .getTitle(),
                saved.getInterviewDateTime().toString(),
                saved.getMode().name(),
                saved.getMeetingLink(),
                saved.getLocation(),
                saved.getStatus().name());
    }

    @Override
    @Transactional
    public void deleteInterview(Long interviewId) {

        User recruiter = getCurrentUser();

        Interview interview =
                interviewRepository
                        .findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found"));

        // Ownership validation
        if (interview.getCreatedBy() == null
                || !interview.getCreatedBy()
                        .getId()
                        .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can delete only your own interviews");
        }

        // Release slot before deleting interview
        InterviewSlot slot =
                interview.getSlot();

        if (slot != null && !slot.isAvailable()) {

            slot.setAvailable(true);
            slotRepository.save(slot);
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
                        new ResourceNotFoundException(
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
                interview.getCreatedAt());
    }

    @Override
    @Transactional
    public InterviewResponseDTO rescheduleInterview(
            Long interviewId,
            InterviewRequestDTO request) {

        User recruiter = getCurrentUser();

        // 1. Find interview
        Interview interview =
                interviewRepository
                        .findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found"));

        // 2. Ownership validation
        if (interview.getCreatedBy() == null
                || !interview.getCreatedBy()
                        .getId()
                        .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can reschedule only your own interview");
        }

        // 3. Completed interview cannot be rescheduled
        if (interview.getStatus()
                == InterviewStatus.COMPLETED) {

            throw new RuntimeException(
                    "Completed interview cannot be rescheduled");
        }

        // 4. Cancelled interview cannot be rescheduled
        if (interview.getStatus()
                == InterviewStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled interview cannot be rescheduled");
        }

        // 5. Validate slot ID
        if (request.getSlotId() == null) {
            throw new IllegalArgumentException(
                    "New interview slot is required");
        }

        // 6. Find new slot
        InterviewSlot newSlot =
                slotRepository
                        .findByIdAndRecruiterId(
                                request.getSlotId(),
                                recruiter.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "New interview slot not found"));

        InterviewSlot oldSlot =
                interview.getSlot();

        /*
         * If recruiter selects the same slot,
         * no new booking/conflict check is required.
         */
        boolean sameSlot =
                oldSlot != null
                        && oldSlot.getId()
                                .equals(newSlot.getId());

        // 7. Validate new slot
        if (!sameSlot && !newSlot.isAvailable()) {
            throw new RuntimeException(
                    "New interview slot is already booked");
        }

        // 8. Conflict validation for new slot
        if (!sameSlot) {

            boolean conflict =
                    interviewRepository.existsInterviewConflict(
                            recruiter.getId(),
                            newSlot.getStartTime(),
                            newSlot.getEndTime(),
                            interviewId);

            if (conflict) {
                throw new RuntimeException(
                        "New interview slot conflicts with another interview");
            }
        }

        // 9. Release old slot
        if (!sameSlot && oldSlot != null) {

            oldSlot.setAvailable(true);
            slotRepository.save(oldSlot);
        }

        // 10. Book new slot
        if (!sameSlot) {

            newSlot.setAvailable(false);
            slotRepository.save(newSlot);

            interview.setSlot(newSlot);
        }

        // 11. Update interview
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

        // 12. Save updated interview
        Interview updated =
                interviewRepository.save(interview);

        // 13. Notify candidate
        User candidate =
                updated.getApplication().getUser();

        emailService.sendInterviewEmail(
                candidate.getEmail(),
                candidate.getName(),
                updated.getApplication()
                        .getJob()
                        .getTitle(),
                updated.getInterviewDateTime().toString(),
                updated.getMode().name(),
                updated.getMeetingLink(),
                updated.getLocation(),
                updated.getStatus().name());

        // 14. Return response
        return mapToDTO(updated);
    }

    @Override
    public RecruiterInterviewDashboardDTO
            getRecruiterDashboard() {

        User recruiter = getCurrentUser();

        Long recruiterId =
                recruiter.getId();

        /*
         * Scheduled interviews
         */
        List<Interview> scheduledInterviews =
                interviewRepository
                        .findByCreatedByIdAndStatusOrderByInterviewDateTimeAsc(
                                recruiterId,
                                InterviewStatus.SCHEDULED);

        /*
         * Rescheduled interviews
         */
        List<Interview> rescheduledInterviews =
                interviewRepository
                        .findByCreatedByIdAndStatusOrderByInterviewDateTimeAsc(
                                recruiterId,
                                InterviewStatus.RESCHEDULED);

        /*
         * Upcoming = Scheduled + Rescheduled
         */
        long upcoming =
                scheduledInterviews.size()
                        + rescheduledInterviews.size();

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

        /*
         * Completed interviews
         */
        List<Interview> completedInterviews =
                interviewRepository
                        .findByCreatedByIdAndStatus(
                                recruiterId,
                                InterviewStatus.COMPLETED);

        /*
         * Pending feedback
         */
        long pendingFeedback =
                completedInterviews
                        .stream()
                        .filter(interview ->
                                feedbackRepository
                                        .findByInterviewId(
                                                interview.getId())
                                        .isEmpty())
                        .count();

        /*
         * Selected candidates
         */
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

        /*
         * Rejected candidates
         */
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

        /*
         * Combine scheduled + rescheduled
         * interviews into upcoming list.
         */
        List<InterviewResponseDTO> upcomingDTO =
                Stream.concat(
                        scheduledInterviews.stream(),
                        rescheduledInterviews.stream())
                        .sorted(
                                Comparator.comparing(
                                        Interview::getInterviewDateTime))
                        .map(this::mapToDTO)
                        .toList();

        RecruiterInterviewDashboardDTO dashboard =
                new RecruiterInterviewDashboardDTO();

        dashboard.setUpcomingInterviews(
                upcoming);

        dashboard.setCompletedInterviews(
                completed);

        dashboard.setCancelledInterviews(
                cancelled);

        dashboard.setPendingFeedback(
                pendingFeedback);

        dashboard.setSelectedCandidates(
                selectedCandidates);

        dashboard.setRejectedCandidates(
                rejectedCandidates);

        dashboard.setUpcomingInterviewList(
                upcomingDTO);

        return dashboard;
    }
    
    @Override
    public List<CalendarEventDTO> getDailyCalendar(LocalDate date) {

        User recruiter = getCurrentUser();

        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.plusDays(1).atStartOfDay();

        List<Interview> interviews =
                interviewRepository.findInterviewsForCalendar(
                        recruiter.getId(),
                        startTime,
                        endTime);

        List<InterviewSlot> slots =
                slotRepository.findSlotsForCalendar(
                        recruiter.getId(),
                        startTime,
                        endTime);

        return buildCalendarEvents(interviews, slots);
    }
    
    @Override
    public List<CalendarEventDTO> getWeeklyCalendar(LocalDate startDate) {

        User recruiter = getCurrentUser();

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime =
                startDate.plusDays(7).atStartOfDay();

        List<Interview> interviews =
                interviewRepository.findInterviewsForCalendar(
                        recruiter.getId(),
                        startTime,
                        endTime);

        List<InterviewSlot> slots =
                slotRepository.findSlotsForCalendar(
                        recruiter.getId(),
                        startTime,
                        endTime);

        return buildCalendarEvents(interviews, slots);
    }
    
    @Override
    public List<CalendarEventDTO> getMonthlyCalendar(YearMonth month) {

        User recruiter = getCurrentUser();

        LocalDateTime startTime =
                month.atDay(1).atStartOfDay();

        LocalDateTime endTime =
                month.plusMonths(1).atDay(1).atStartOfDay();

        List<Interview> interviews =
                interviewRepository.findInterviewsForCalendar(
                        recruiter.getId(),
                        startTime,
                        endTime);

        List<InterviewSlot> slots =
                slotRepository.findSlotsForCalendar(
                        recruiter.getId(),
                        startTime,
                        endTime);

        return buildCalendarEvents(interviews, slots);
    }
    
    private List<CalendarEventDTO> buildCalendarEvents(
            List<Interview> interviews,
            List<InterviewSlot> slots) {

        List<CalendarEventDTO> events = new ArrayList<>();

        for (Interview interview : interviews) {

            CalendarEventDTO event = new CalendarEventDTO();

            event.setId(interview.getId());
            event.setType("INTERVIEW");

            event.setStartTime(
                    interview.getInterviewDateTime());

            if (interview.getSlot() != null) {
                event.setEndTime(
                        interview.getSlot().getEndTime());
            }

            event.setTitle(
                    interview.getApplication()
                            .getJob()
                            .getTitle());

            event.setStatus(interview.getStatus());
            event.setMode(interview.getMode());

            event.setApplicationId(
                    interview.getApplication().getId());

            event.setJobTitle(
                    interview.getApplication()
                            .getJob()
                            .getTitle());

            event.setAvailable(false);

            events.add(event);
        }

        for (InterviewSlot slot : slots) {

            CalendarEventDTO event = new CalendarEventDTO();

            event.setId(slot.getId());
            event.setType("AVAILABLE_SLOT");

            event.setStartTime(slot.getStartTime());
            event.setEndTime(slot.getEndTime());

            event.setTitle("Interview Slot");

            event.setAvailable(slot.isAvailable());

            events.add(event);
        }

        events.sort(
            Comparator.comparing(
                CalendarEventDTO::getStartTime));

        return events;
    }
}