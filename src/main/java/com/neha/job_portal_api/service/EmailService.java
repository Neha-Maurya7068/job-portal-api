package com.neha.job_portal_api.service;

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
}