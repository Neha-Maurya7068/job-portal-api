package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.entity.Interview;
import com.neha.job_portal_api.entity.InterviewStatus;
import com.neha.job_portal_api.repository.InterviewRepository;
import com.neha.job_portal_api.service.EmailService;
import com.neha.job_portal_api.service.InterviewReminderService;
import com.neha.job_portal_api.service.NotificationService;

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

            // Candidate reminder
            // Email + notification

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

            // Candidate reminder
            // Email + notification

            interview.setReminder1HourSent(true);
            interviewRepository.save(interview);
        }
    }
}