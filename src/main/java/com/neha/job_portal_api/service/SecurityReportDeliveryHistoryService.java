
package com.neha.job_portal_api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.job_portal_api.entity.SecurityReportDelivery;
import com.neha.job_portal_api.entity.SecurityReportDeliveryStatus;
import com.neha.job_portal_api.repository.SecurityReportDeliveryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityReportDeliveryHistoryService {

    private final SecurityReportDeliveryRepository repository;

    @Transactional
    public Long createPendingRecord(String recipient, LocalDate reportDate) {
        SecurityReportDelivery record = SecurityReportDelivery.builder()
                .recipientEmail(recipient)
                .reportDate(reportDate)
                .status(SecurityReportDeliveryStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(record).getId();
    }

    @Transactional
    public void markSent(Long id) {
        SecurityReportDelivery record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Report delivery record not found: " + id));

        record.setStatus(SecurityReportDeliveryStatus.SENT);
        record.setSentAt(LocalDateTime.now());
        record.setFailureReason(null);
    }

    @Transactional
    public void markFailed(Long id, Exception exception) {
        SecurityReportDelivery record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Report delivery record not found: " + id));

        record.setStatus(SecurityReportDeliveryStatus.FAILED);

        String reason = exception.getMessage();
        if (reason == null || reason.isBlank()) {
            reason = exception.getClass().getSimpleName();
        }

        record.setFailureReason(
                reason.length() > 2000 ? reason.substring(0, 2000) : reason);
    }

    @Transactional(readOnly = true)
    public List<SecurityReportDelivery> getRecentDeliveries() {
        return repository.findTop50ByOrderByCreatedAtDesc();
    }
}
