package com.neha.job_portal_api.service;

import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;

import com.neha.job_portal_api.entity.Job;

@EnableScheduling
public interface EmailService {

    void sendApplicationStatusEmail(
            String to,
            String applicantName,
            String jobTitle,
            String status);
    
    void sendJobAlertEmail(
            String to,
            String applicantName,
            String jobTitle,
            String companyName,
            String location);
    
    void sendDailyJobDigestEmail(
            String to,
            String userName,
            List<Job> jobs);
    
    void sendInterviewEmail(
            String to,
            String candidateName,
            String jobTitle,
            String interviewDateTime,
            String mode,
            String meetingLink,
            String location,
            String status);
    
    void sendInterviewReminderEmail(
            String to,
            String candidateName,
            String jobTitle,
            String interviewDateTime,
            String mode,
            String meetingLink,
            String location,
            String reminderType);
    
    void sendPasswordResetEmail(
            String to,
            String userName,
            String resetLink
    );
}