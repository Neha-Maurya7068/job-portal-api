package com.neha.job_portal_api.service;

import com.neha.job_portal_api.entity.User;

public interface LoginAttemptService {

    void recordAttempt(
            String email,
            User user,
            String ipAddress,
            String userAgent,
            boolean success,
            String failureReason
    );

    long getRecentFailedAttempts(String email);
}