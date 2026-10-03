package com.neha.job_portal_api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.NotificationPreferenceDTO;
import com.neha.job_portal_api.service.NotificationPreferenceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('JOB_SEEKER','RECRUITER','ADMIN')")
    public NotificationPreferenceDTO getMyPreferences() {
        return preferenceService.getMyPreferences();
    }

    @PutMapping("/my")
    @PreAuthorize("hasAnyRole('JOB_SEEKER','RECRUITER','ADMIN')")
    public NotificationPreferenceDTO updateMyPreferences(
            @RequestBody NotificationPreferenceDTO request) {

        return preferenceService.updateMyPreferences(request);
    }
}