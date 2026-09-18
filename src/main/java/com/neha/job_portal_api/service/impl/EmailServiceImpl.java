package com.neha.job_portal_api.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    private static final Logger logger =
	        LoggerFactory.getLogger(EmailServiceImpl.class);
	

    @Override
    @Async
    public void sendApplicationStatusEmail(
            String to,
            String applicantName,
            String jobTitle,
            String status) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);

            helper.setSubject(
                    "Application Status Updated - "
                    + jobTitle);

            String htmlContent =
                    """
                    <html>
                    <body style="font-family: Arial, sans-serif;
                                 background-color: #f4f6f8;
                                 padding: 30px;">

                        <div style="max-width: 600px;
                                    margin: auto;
                                    background: white;
                                    padding: 30px;
                                    border-radius: 10px;">

                            <h2>Application Status Updated</h2>

                            <p>
                                Hello <b>%s</b>,
                            </p>

                            <p>
                                Your application for
                                <b>%s</b> has been updated.
                            </p>

                            <p>
                                <b>Current Status:</b> %s
                            </p>

                            <p>
                                Regards,<br>
                                <b>Job Portal Team</b>
                            </p>

                        </div>

                    </body>
                    </html>
                    """.formatted(
                            applicantName,
                            jobTitle,
                            status);

            helper.setText(htmlContent, true);

            mailSender.send(message);

            logger.info(
                    "Application status email sent successfully to {}",
                    to);

        } catch (Exception e) {

            logger.error(
                    "Failed to send application status email to {}",
                    to,
                    e);
        }
    }
}