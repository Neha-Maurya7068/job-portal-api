package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.entity.SecurityReportDelivery;
import com.neha.job_portal_api.service.SecurityReportDeliveryHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/security-report-deliveries")
@RequiredArgsConstructor
public class SecurityReportDeliveryController {

    private final SecurityReportDeliveryHistoryService historyService;

    @GetMapping
    public List<SecurityReportDelivery> getRecentDeliveries() {
        return historyService.getRecentDeliveries();
    }
}
