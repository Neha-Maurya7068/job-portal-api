package com.neha.job_portal_api.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.NotificationPreferenceDTO;
import com.neha.job_portal_api.entity.NotificationPreference;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.NotificationPreferenceRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.NotificationPreferenceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceServiceImpl
        implements NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Override
    public NotificationPreferenceDTO getMyPreferences() {

        User user = getCurrentUser();

        NotificationPreference preference =
                preferenceRepository.findByUserId(user.getId())
                        .orElseGet(() -> createDefaultPreference(user));

        return mapToDTO(preference);
    }

    @Override
    public NotificationPreferenceDTO updateMyPreferences(
            NotificationPreferenceDTO request) {

        User user = getCurrentUser();

        NotificationPreference preference =
                preferenceRepository.findByUserId(user.getId())
                        .orElseGet(() -> createDefaultPreference(user));

        preference.setEmailEnabled(request.isEmailEnabled());
        preference.setInAppEnabled(request.isInAppEnabled());
        preference.setJobAlertEnabled(request.isJobAlertEnabled());
        preference.setInterviewReminderEnabled(
                request.isInterviewReminderEnabled());
        preference.setApplicationStatusEnabled(
                request.isApplicationStatusEnabled());

        NotificationPreference savedPreference =
                preferenceRepository.save(preference);

        return mapToDTO(savedPreference);
    }

    private NotificationPreference createDefaultPreference(User user) {

        NotificationPreference preference =
                new NotificationPreference();

        preference.setEmailEnabled(true);
        preference.setInAppEnabled(true);
        preference.setJobAlertEnabled(true);
        preference.setInterviewReminderEnabled(true);
        preference.setApplicationStatusEnabled(true);
        preference.setUser(user);

        return preferenceRepository.save(preference);
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private NotificationPreferenceDTO mapToDTO(
            NotificationPreference preference) {

        return new NotificationPreferenceDTO(
                preference.isEmailEnabled(),
                preference.isInAppEnabled(),
                preference.isJobAlertEnabled(),
                preference.isInterviewReminderEnabled(),
                preference.isApplicationStatusEnabled()
        );
    }
}