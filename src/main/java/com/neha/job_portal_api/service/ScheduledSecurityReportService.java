
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
    private final SecurityReportDeliveryHistoryService historyService;


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

        Long deliveryId = null;

        try {
            deliveryId = historyService.createPendingRecord(
                    recipientEmail, yesterday);

            emailService.sendPdfReport(
                    recipientEmail, yesterday, yesterday, null);

            historyService.markSent(deliveryId);

            log.info("Daily security report sent for {}", yesterday);

        } catch (Exception ex) {
            log.error("Daily security report failed for {}", yesterday, ex);

            if (deliveryId != null) {
                try {
                    historyService.markFailed(deliveryId, ex);
                } catch (Exception historyException) {
                    log.error("Could not update report delivery history", historyException);
                }
            }
        }
    }
}
