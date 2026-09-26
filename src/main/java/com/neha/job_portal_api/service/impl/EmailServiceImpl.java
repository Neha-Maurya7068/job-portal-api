package com.neha.job_portal_api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.entity.Job;
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
        
        @Override
        @Async
        public void sendJobAlertEmail(
                String to,
                String applicantName,
                String jobTitle,
                String companyName,
                String location) {

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

                try {

                    MimeMessage message =
                            mailSender.createMimeMessage();

                    MimeMessageHelper helper =
                            new MimeMessageHelper(message, true);

                    helper.setTo(to);
                    helper.setSubject("Your Daily Job Digest");

                    StringBuilder html = new StringBuilder();

                    html.append("<h2>Your Daily Job Digest 🚀</h2>");
                    html.append("<p>Hello ")
                        .append(userName)
                        .append(",</p>");

                    html.append("<p>Here are the latest jobs matching your alerts:</p>");

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

                    html.append("<p>Your interview status has been updated.</p>");

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
}