package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.LoginActivityDTO;
import com.neha.job_portal_api.entity.User;

public interface LoginActivityService {

    void recordLogin(
            User user,
            String token,
            String ipAddress,
            String userAgent);

    List<LoginActivityDTO> getMyLoginActivities();

    void logoutCurrentSession(String token);

    void logoutAllSessions();
}