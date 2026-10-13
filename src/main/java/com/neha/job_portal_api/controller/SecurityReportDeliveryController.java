
package com.neha.job_portal_api.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;

import com.neha.job_portal_api.dto.SecurityReportDeliverySummaryDTO;
import com.neha.job_portal_api.entity.SecurityReportDelivery;
import com.neha.job_portal_api.entity.SecurityReportDeliveryStatus;
import com.neha.job_portal_api.service.SecurityReportDeliveryHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/security-report-deliveries")
@RequiredArgsConstructor
public class SecurityReportDeliveryController {

    private final SecurityReportDeliveryHistoryService historyService;

    @GetMapping
    public Page<SecurityReportDelivery> getDeliveries(
            @RequestParam(required = false)
            SecurityReportDeliveryStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            return historyService.searchDeliveries(
                    status, startDate, endDate, page, size);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/summary")
    public SecurityReportDeliverySummaryDTO getSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        try {
            return historyService.getDeliverySummary(startDate, endDate);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
