package com.neha.job_portal_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.dto.AdminDashboardDTO;
import com.neha.job_portal_api.dto.ApiResponse;
import com.neha.job_portal_api.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardDTO> getDashboard() {

        AdminDashboardDTO dashboard =
                adminDashboardService.getDashboard();

        return new ApiResponse<>(
                true,
                "Admin dashboard fetched successfully",
                dashboard
        );
    }
}
