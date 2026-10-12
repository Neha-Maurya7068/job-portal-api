
package com.neha.job_portal_api.service;

import java.time.LocalDate;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityReportEmailService {

    private final JavaMailSender mailSender;
    private final SecurityReportService securityReportService;

    public void sendPdfReport(
            String recipient,
            LocalDate startDate,
            LocalDate endDate,
            String severity) throws MessagingException {

        byte[] pdf = securityReportService.generatePdf(
                startDate, endDate, severity);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(recipient);
        helper.setSubject("Job Portal Security Report");
        helper.setText(
                "Hello,\n\nPlease find the requested security report attached."
                + "\n\nDate range: " + startDate + " to " + endDate
                + "\nSeverity: "
                + (severity == null || severity.isBlank()
                    ? "ALL" : severity)
                + "\n\nRegards,\nJob Portal Security Team"
        );

        helper.addAttachment("security-report.pdf",
                new org.springframework.core.io.ByteArrayResource(pdf));

        mailSender.send(message);
    }
}
