
package com.neha.job_portal_api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.job_portal_api.dto.SecurityReportDeliverySummaryDTO;
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



// Add these methods inside SecurityReportDeliveryHistoryService.

@Transactional(readOnly = true)
public Page<SecurityReportDelivery> searchDeliveries(
        SecurityReportDeliveryStatus status,
        LocalDate startDate,
        LocalDate endDate,
        int page,
        int size) {

    validateDates(startDate, endDate);

    int safePage = Math.max(page, 0);
    int safeSize = Math.max(1, Math.min(size, 100));

    Pageable pageable = PageRequest.of(safePage, safeSize);

    return repository.searchDeliveries(
            status, startDate, endDate, pageable);
}

@Transactional(readOnly = true)
public SecurityReportDeliverySummaryDTO getDeliverySummary(
        LocalDate startDate,
        LocalDate endDate) {

    validateDates(startDate, endDate);

    return SecurityReportDeliverySummaryDTO.builder()
            .totalReports(repository.countInDateRange(startDate, endDate))
            .sentReports(repository.countByStatusInDateRange(
                    SecurityReportDeliveryStatus.SENT, startDate, endDate))
            .failedReports(repository.countByStatusInDateRange(
                    SecurityReportDeliveryStatus.FAILED, startDate, endDate))
            .pendingReports(repository.countByStatusInDateRange(
                    SecurityReportDeliveryStatus.PENDING, startDate, endDate))
            .build();
}

private void validateDates(LocalDate startDate, LocalDate endDate) {
    if (startDate != null && endDate != null
            && startDate.isAfter(endDate)) {
        throw new IllegalArgumentException(
                "startDate must be before or equal to endDate");
    }
}
}


