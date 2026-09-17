package com.neha.job_portal_api.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendApplicationStatusEmail(
            String to,
            String applicantName,
            String jobTitle,
            String status) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

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

                            <h2 style="color: #333;">
                                Application Status Updated
                            </h2>

                            <p>
                                Hello <b>%s</b>,
                            </p>

                            <p>
                                Your application for
                                <b>%s</b> has been updated.
                            </p>

                            <div style="background-color: #f0f4ff;
                                        padding: 15px;
                                        border-radius: 8px;
                                        margin: 20px 0;">

                                <p>
                                    <b>Current Status:</b>
                                    %s
                                </p>

                            </div>

                            <p>
                                Please log in to your Job Portal
                                account for more details.
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

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Failed to send application status email",
                    e);
        }
    }
}