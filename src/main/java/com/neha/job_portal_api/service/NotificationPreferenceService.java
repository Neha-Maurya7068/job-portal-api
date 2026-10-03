package com.neha.job_portal_api.service;

import com.neha.job_portal_api.dto.NotificationPreferenceDTO;

public interface NotificationPreferenceService {

    NotificationPreferenceDTO getMyPreferences();

    NotificationPreferenceDTO updateMyPreferences(
            NotificationPreferenceDTO request);
}