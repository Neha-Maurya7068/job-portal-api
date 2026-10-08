package com.neha.job_portal_api.service;

import com.neha.job_portal_api.entity.User;

public interface SuspiciousLoginService {

    void checkFailedLogin(
            String email,
            User user,
            String ipAddress,
            String userAgent
    );

    void checkSuccessfulLogin(
            User user,
            String ipAddress,
            String userAgent
    );

    void handleFailedLogin(
            String email,
            User user,
            String ipAddress,
            String userAgent
    );
}