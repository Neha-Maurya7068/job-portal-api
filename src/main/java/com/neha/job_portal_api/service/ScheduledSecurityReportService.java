
package com.neha.job_portal_api.service;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        name = "security.report.enabled",
        havingValue = "true"
)
public class ScheduledSecurityReportService {

    private final SecurityReportEmailService emailService;

    @Value("${security.report.recipient-email}")
    private String recipientEmail;

    @Value("${security.report.time-zone:Asia/Kolkata}")
    private String timeZone;

    @Scheduled(
        cron = "${security.report.cron:0 0 8 * * *}",
        zone = "${security.report.time-zone:Asia/Kolkata}"
    )
     @SchedulerLock(
    	    name = "dailySecurityReport",
    	    lockAtMostFor = "PT30M",
    	    lockAtLeastFor = "PT1M"
    	    )
    	    
    public void sendDailySecurityReport() {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            log.error("Scheduled report skipped: recipient email is missing");
            return;
        }

        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDate yesterday = LocalDate.now(zoneId).minusDays(1);

        try {
            emailService.sendPdfReport(
                    recipientEmail,
                    yesterday,
                    yesterday,
                    null
            );

            log.info(
                    "Daily security report sent for date {}",
                    yesterday
            );

        } catch (MessagingException | MailException ex) {
            log.error(
                    "Failed to send daily security report for date {}",
                    yesterday,
                    ex
            );
        }
    }
}
