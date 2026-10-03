package com.neha.job_portal_api.dto;

import lombok.Data;

@Data
public class NotificationPreferenceDTO {

    private boolean emailEnabled;

    private boolean inAppEnabled;

    private boolean jobAlertEnabled;

    private boolean interviewReminderEnabled;

    private boolean applicationStatusEnabled;
}