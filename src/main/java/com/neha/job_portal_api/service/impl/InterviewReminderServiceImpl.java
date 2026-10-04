package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.entity.Interview;
import com.neha.job_portal_api.entity.InterviewStatus;
import com.neha.job_portal_api.entity.NotificationPreference;
import com.neha.job_portal_api.entity.NotificationType;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.InterviewRepository;
import com.neha.job_portal_api.repository.NotificationPreferenceRepository;
import com.neha.job_portal_api.service.EmailService;
import com.neha.job_portal_api.service.InterviewReminderService;
import com.neha.job_portal_api.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewReminderServiceImpl
        implements InterviewReminderService {

    private final InterviewRepository interviewRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Override
    @Scheduled(cron = "0 */10 * * * *")
    public void sendInterviewReminders() {

        LocalDateTime now = LocalDateTime.now();

        send24HourReminders(now);
        send1HourReminders(now);
    }

    private void send24HourReminders(LocalDateTime now) {

        LocalDateTime start = now.plusHours(24).minusMinutes(10);
        LocalDateTime end = now.plusHours(24).plusMinutes(10);

        List<Interview> interviews =
                interviewRepository
                        .findByStatusAndInterviewDateTimeBetween(
                                InterviewStatus.SCHEDULED,
                                start,
                                end);

        for (Interview interview : interviews) {

            if (interview.isReminder24HoursSent()) {
                continue;
            }

            sendReminder(interview, "24 hours");

            interview.setReminder24HoursSent(true);
            interviewRepository.save(interview);
        }
    }

    private void send1HourReminders(LocalDateTime now) {

        LocalDateTime start = now.plusHours(1).minusMinutes(10);
        LocalDateTime end = now.plusHours(1).plusMinutes(10);

        List<Interview> interviews =
                interviewRepository
                        .findByStatusAndInterviewDateTimeBetween(
                                InterviewStatus.SCHEDULED,
                                start,
                                end);

        for (Interview interview : interviews) {

            if (interview.isReminder1HourSent()) {
                continue;
            }

            sendReminder(interview, "1 hour");

            interview.setReminder1HourSent(true);
            interviewRepository.save(interview);
        }
    }

    private void sendReminder(
            Interview interview,
            String reminderTime) {

        User candidate =
                interview.getApplication().getUser();

        NotificationPreference preference =
                preferenceRepository
                        .findByUserId(candidate.getId())
                        .orElseGet(() ->
                                createDefaultPreference(candidate));

        // Interview reminders completely OFF
        if (!preference.isInterviewReminderEnabled()) {
            return;
        }

        String message =
                "Your interview for "
                        + interview.getApplication()
                                .getJob()
                                .getTitle()
                        + " is scheduled in "
                        + reminderTime
                        + ".";

        // In-app notification
        if (preference.isInAppEnabled()) {

            notificationService.createNotification(
                    candidate,
                    message,
                    NotificationType.INTERVIEW_SCHEDULED);
        }

        // Email notification
        if (preference.isEmailEnabled()) {

            emailService.sendInterviewEmail(
                    candidate.getEmail(),
                    candidate.getName(),
                    interview.getApplication()
                            .getJob()
                            .getTitle(),
                    interview.getInterviewDateTime()
                            .toString(),
                    interview.getMode().toString(),
                    interview.getMeetingLink(),
                    interview.getLocation(),
                    "INTERVIEW REMINDER - " + reminderTime);
        }
    }

    private NotificationPreference createDefaultPreference(
            User user) {

        NotificationPreference preference =
                new NotificationPreference();

        preference.setEmailEnabled(true);
        preference.setInAppEnabled(true);
        preference.setJobAlertEnabled(true);
        preference.setInterviewReminderEnabled(true);
        preference.setApplicationStatusEnabled(true);
        preference.setUser(user);

        return preferenceRepository.save(preference);
    }
}