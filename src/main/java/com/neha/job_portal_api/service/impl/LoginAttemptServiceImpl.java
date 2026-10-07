package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.neha.job_portal_api.entity.LoginAttempt;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.LoginAttemptRepository;
import com.neha.job_portal_api.service.LoginAttemptService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl
        implements LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;

    @Override
    public void recordAttempt(
            String email,
            User user,
            String ipAddress,
            String userAgent,
            boolean success,
            String failureReason) {

        LoginAttempt attempt = LoginAttempt.builder()
                .email(email)
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .attemptedAt(LocalDateTime.now())
                .success(success)
                .failureReason(failureReason)
                .build();

        loginAttemptRepository.save(attempt);
    }

    @Override
    public long getRecentFailedAttempts(String email) {

        LocalDateTime fifteenMinutesAgo =
                LocalDateTime.now().minusMinutes(15);

        return loginAttemptRepository
                .countByEmailAndSuccessFalseAndAttemptedAtAfter(
                        email,
                        fifteenMinutesAgo
                );
    }
}