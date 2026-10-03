package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.job_portal_api.entity.Interview;
import com.neha.job_portal_api.entity.InterviewStatus;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.InterviewRepository;
import com.neha.job_portal_api.service.EmailService;
import com.neha.job_portal_api.service.InterviewReminderService;
import com.neha.job_portal_api.service.NotificationService;
import com.neha.job_portal_api.entity.NotificationType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewReminderServiceImpl
        implements InterviewReminderService {

    private final InterviewRepository interviewRepository;

    private final EmailService emailService;

    private final NotificationService notificationService;

    @Override
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void sendInterviewReminders() {

        LocalDateTime now = LocalDateTime.now();

        send24HourReminders(now);

        send1HourReminders(now);
    }

    private void send24HourReminders(LocalDateTime now) {

        LocalDateTime start = now.plusHours(24);

        LocalDateTime end = start.plusMinutes(10);

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

        LocalDateTime start = now.plusHours(1);

        LocalDateTime end = start.plusMinutes(10);

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

        String jobTitle =
                interview.getApplication()
                        .getJob()
                        .getTitle();

        String message =
                "Your interview for "
                + jobTitle
                + " is scheduled in "
                + reminderTime
                + ".";

        notificationService.createNotification(
                candidate,
                "Interview Reminder",
                message
        );

        emailService.sendInterviewReminderEmail(
                candidate.getEmail(),
                candidate.getName(),
                jobTitle,
                interview.getInterviewDateTime().toString(),
                interview.getMode().name(),
                interview.getMeetingLink(),
                interview.getLocation(),
                reminderTime
        );

        User recruiter = interview.getCreatedBy();

        notificationService.createNotification(
                recruiter,
                "Interview Reminder",
                "Interview with "
                + candidate.getName()
                + " is scheduled in "
                + reminderTime
                + "."
        );
    }
}