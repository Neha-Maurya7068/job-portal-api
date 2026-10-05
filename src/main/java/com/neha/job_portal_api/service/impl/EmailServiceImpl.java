package com.neha.job_portal_api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.entity.Job;
import com.neha.job_portal_api.entity.NotificationPreference;
import com.neha.job_portal_api.repository.NotificationPreferenceRepository;
import com.neha.job_portal_api.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final NotificationPreferenceRepository preferenceRepository;

    private static final Logger logger =
            LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    @Async
    public void sendApplicationStatusEmail(
            String to,
            String applicantName,
            String jobTitle,
            String status) {

        if (!isEmailEnabled(to)) {
            logger.info(
                    "Application status email skipped because email notifications are disabled for {}",
                    to);
            return;
        }

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

    @Override
    @Async
    public void sendJobAlertEmail(
            String to,
            String applicantName,
            String jobTitle,
            String companyName,
            String location) {

        if (!isEmailEnabled(to)) {
            logger.info(
                    "Job alert email skipped because email notifications are disabled for {}",
                    to);
            return;
        }

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("New Job Matching Your Alert");

            String htmlContent =
                    "<h2>New Job Opportunity 🎯</h2>"
                    + "<p>Hello " + applicantName + ",</p>"
                    + "<p>A new job matching your job alert is available.</p>"
                    + "<hr>"
                    + "<h3>" + jobTitle + "</h3>"
                    + "<p><b>Company:</b> "
                    + companyName + "</p>"
                    + "<p><b>Location:</b> "
                    + location + "</p>"
                    + "<p>Login to your Job Portal to view the complete job details.</p>"
                    + "<br>"
                    + "<p>Happy Job Hunting! 🚀</p>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

            logger.info(
                    "Job alert email sent successfully to {}",
                    to);

        } catch (Exception e) {

            logger.error(
                    "Failed to send job alert email to {}",
                    to,
                    e);
        }
    }

    @Override
    @Async
    public void sendDailyJobDigestEmail(
            String to,
            String userName,
            List<Job> jobs) {

        if (!isEmailEnabled(to)) {
            logger.info(
                    "Daily job digest email skipped because email notifications are disabled for {}",
                    to);
            return;
        }

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Your Daily Job Digest");

            StringBuilder html =
                    new StringBuilder();

            html.append("<h2>Your Daily Job Digest 🚀</h2>");
            html.append("<p>Hello ")
                .append(userName)
                .append(",</p>");

            html.append(
                    "<p>Here are the latest jobs matching your alerts:</p>");

            html.append("<ul>");

            for (Job job : jobs) {

                html.append("<li>")
                    .append("<b>")
                    .append(job.getTitle())
                    .append("</b>")
                    .append(" - ")
                    .append(job.getCompanyName())
                    .append(" - ")
                    .append(job.getLocation())
                    .append("</li>");
            }

            html.append("</ul>");

            html.append("<p>Happy Job Hunting! 🎯</p>");

            helper.setText(html.toString(), true);

            mailSender.send(message);

            logger.info(
                    "Daily job digest sent successfully to {}",
                    to);

        } catch (Exception e) {

            logger.error(
                    "Failed to send daily job digest to {}",
                    to,
                    e);
        }
    }

    @Override
    @Async
    public void sendInterviewEmail(
            String to,
            String candidateName,
            String jobTitle,
            String interviewDateTime,
            String mode,
            String meetingLink,
            String location,
            String status) {

        if (!isEmailEnabled(to)) {
            logger.info(
                    "Interview email skipped because email notifications are disabled for {}",
                    to);
            return;
        }

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);

            helper.setSubject(
                    "Interview " + status + " - " + jobTitle);

            StringBuilder html =
                    new StringBuilder();

            html.append("<h2>Interview Update 📅</h2>");

            html.append("<p>Hello ")
                    .append(candidateName)
                    .append(",</p>");

            html.append(
                    "<p>Your interview status has been updated.</p>");

            html.append("<hr>");

            html.append("<p><b>Job:</b> ")
                    .append(jobTitle)
                    .append("</p>");

            html.append("<p><b>Date & Time:</b> ")
                    .append(interviewDateTime)
                    .append("</p>");

            html.append("<p><b>Mode:</b> ")
                    .append(mode)
                    .append("</p>");

            if (meetingLink != null
                    && !meetingLink.isBlank()) {

                html.append("<p><b>Meeting Link:</b> ")
                        .append(meetingLink)
                        .append("</p>");
            }

            if (location != null
                    && !location.isBlank()) {

                html.append("<p><b>Location:</b> ")
                        .append(location)
                        .append("</p>");
            }

            html.append("<p><b>Status:</b> ")
                    .append(status)
                    .append("</p>");

            html.append("<br>");
            html.append("<p>Best wishes! 🚀</p>");

            helper.setText(html.toString(), true);

            mailSender.send(message);

            logger.info(
                    "Interview email sent successfully to {}",
                    to);

        } catch (Exception e) {

            logger.error(
                    "Failed to send interview email to {}",
                    to,
                    e);
        }
    }

    @Override
    @Async
    public void sendInterviewReminderEmail(
            String to,
            String candidateName,
            String jobTitle,
            String interviewDateTime,
            String mode,
            String meetingLink,
            String location,
            String reminderType) {

        if (!isEmailEnabled(to)) {
            logger.info(
                    "Interview reminder email skipped because email notifications are disabled for {}",
                    to);
            return;
        }

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);

            helper.setSubject(
                    "Interview Reminder - " + jobTitle);

            StringBuilder html =
                    new StringBuilder();

            html.append("<h2>Interview Reminder ⏰</h2>");

            html.append("<p>Hello ")
                    .append(candidateName)
                    .append(",</p>");

            html.append(
                    "<p>Your interview is scheduled in <b>")
                    .append(reminderType)
                    .append("</b>.</p>");

            html.append("<hr>");

            html.append("<p><b>Job:</b> ")
                    .append(jobTitle)
                    .append("</p>");

            html.append("<p><b>Date & Time:</b> ")
                    .append(interviewDateTime)
                    .append("</p>");

            html.append("<p><b>Mode:</b> ")
                    .append(mode)
                    .append("</p>");

            if (meetingLink != null
                    && !meetingLink.isBlank()) {

                html.append("<p><b>Meeting Link:</b> ")
                        .append(meetingLink)
                        .append("</p>");
            }

            if (location != null
                    && !location.isBlank()) {

                html.append("<p><b>Location:</b> ")
                        .append(location)
                        .append("</p>");
            }

            html.append("<br>");

            html.append(
                    "<p>Please be ready before the scheduled time.</p>");

            html.append("<p>Best wishes! 🚀</p>");

            helper.setText(html.toString(), true);

            mailSender.send(message);

            logger.info(
                    "Interview reminder email sent successfully to {}",
                    to);

        } catch (Exception e) {

            logger.error(
                    "Failed to send interview reminder email to {}",
                    to,
                    e);
        }
    }

    private boolean isEmailEnabled(String email) {

        NotificationPreference preference =
                preferenceRepository
                        .findByUserEmail(email)
                        .orElse(null);

        if (preference == null) {
            return true;
        }

        return preference.isEmailEnabled();
    }
    
    @Async
    @Override
    public void sendPasswordResetEmail(
            String to,
            String userName,
            String resetLink) {

        try {

            if (!isEmailEnabled(to)) {
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Password Reset Request");

            String htmlContent =
                    "<html>"
                    + "<body style='font-family: Arial, sans-serif;'>"

                    + "<h2>Password Reset Request</h2>"

                    + "<p>Hello " + userName + ",</p>"

                    + "<p>"
                    + "We received a request to reset the password "
                    + "for your Job Portal account."
                    + "</p>"

                    + "<p>"
                    + "Click the button below to reset your password:"
                    + "</p>"

                    + "<p>"
                    + "<a href='" + resetLink + "' "
                    + "style='background:#2563eb;"
                    + "color:white;"
                    + "padding:12px 20px;"
                    + "text-decoration:none;"
                    + "border-radius:5px;'>"
                    + "Reset Password"
                    + "</a>"
                    + "</p>"

                    + "<p>"
                    + "This link will expire in 30 minutes."
                    + "</p>"

                    + "<p>"
                    + "If you did not request a password reset, "
                    + "you can safely ignore this email."
                    + "</p>"

                    + "<p>Regards,<br>"
                    + "Job Portal Team</p>"

                    + "</body>"
                    + "</html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {

            logger.error(
                    "Failed to send password reset email to {}",
                    to,
                    e);
        }
    }
}