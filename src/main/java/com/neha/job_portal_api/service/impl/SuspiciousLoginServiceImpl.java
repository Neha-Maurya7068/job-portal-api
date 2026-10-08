package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.neha.job_portal_api.entity.LoginActivity;
import com.neha.job_portal_api.entity.SecurityEventType;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.LoginActivityRepository;
import com.neha.job_portal_api.repository.SecurityEventRepository;
import com.neha.job_portal_api.service.AccountLockService;
import com.neha.job_portal_api.service.EmailService;
import com.neha.job_portal_api.service.LoginAttemptService;
import com.neha.job_portal_api.service.SecurityEventService;
import com.neha.job_portal_api.service.SuspiciousLoginService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuspiciousLoginServiceImpl
        implements SuspiciousLoginService {

    private final LoginAttemptService loginAttemptService;
    private final SecurityEventService securityEventService;
    private final LoginActivityRepository loginActivityRepository;
    private final SecurityEventRepository securityEventRepository;
    private final AccountLockService accountLockService;
    private final EmailService emailService;

    private static final int FAILED_LOGIN_THRESHOLD = 5;

    @Override
    public void checkFailedLogin(
            String email,
            User user,
            String ipAddress,
            String userAgent) {

        loginAttemptService.recordAttempt(
                email,
                user,
                ipAddress,
                userAgent,
                false,
                "INVALID_CREDENTIALS"
        );

        long failedAttempts =
                loginAttemptService
                        .getRecentFailedAttempts(email);

        if (failedAttempts >= FAILED_LOGIN_THRESHOLD
                && user != null) {

            LocalDateTime fifteenMinutesAgo =
                    LocalDateTime.now().minusMinutes(15);

            boolean alreadyReported =
                    securityEventRepository
                            .existsByEmailAndTypeAndCreatedAtAfter(
                                    email,
                                    SecurityEventType
                                            .MULTIPLE_FAILED_LOGINS,
                                    fifteenMinutesAgo
                            );

            if (!alreadyReported) {

                String description =
                        "Multiple failed login attempts detected. "
                        + "At least "
                        + FAILED_LOGIN_THRESHOLD
                        + " failed attempts occurred within 15 minutes.";

                securityEventService.createEvent(
                        email,
                        SecurityEventType.MULTIPLE_FAILED_LOGINS,
                        description,
                        ipAddress,
                        userAgent
                );

                sendSecurityEmail(
                        user,
                        "Multiple failed login attempts",
                        description,
                        ipAddress,
                        userAgent
                );
            }
        }
    }

    
    @Override
    public void handleFailedLogin(
            String email,
            User user,
            String ipAddress,
            String userAgent) {

        loginAttemptService.recordAttempt(
                email,
                user,
                ipAddress,
                userAgent,
                false,
                "INVALID_CREDENTIALS"
        );

        if (user == null) {
            return;
        }

        long failedAttempts =
                loginAttemptService
                        .getRecentFailedAttempts(email);

        if (failedAttempts >= 5
                && !accountLockService.isLocked(user)) {

            accountLockService.lockAccount(user);

            String description =
                    "Account temporarily locked after "
                    + "multiple failed login attempts.";

            securityEventService.createEvent(
                    email,
                    SecurityEventType.MULTIPLE_FAILED_LOGINS,
                    description,
                    ipAddress,
                    userAgent
            );

            sendSecurityEmail(
                    user,
                    "Account temporarily locked",
                    description
                            + "\n\nYour account has been "
                            + "temporarily locked for 30 minutes.",
                    ipAddress,
                    userAgent
            );
        }
    }
    
    
    @Override
    public void checkSuccessfulLogin(
            User user,
            String ipAddress,
            String userAgent) {

        loginAttemptService.recordAttempt(
                user.getEmail(),
                user,
                ipAddress,
                userAgent,
                true,
                null
        );

        List<LoginActivity> activities =
                loginActivityRepository
                        .findByUserIdOrderByLoginAtDesc(
                                user.getId()
                        );

        /*
         * First login is normal.
         */
        if (activities.isEmpty()) {
            return;
        }

        boolean knownIp = activities.stream()
                .anyMatch(activity ->
                        ipAddress != null
                                && ipAddress.equals(
                                        activity.getIpAddress()
                                ));

        boolean knownDevice = activities.stream()
                .anyMatch(activity ->
                        userAgent != null
                                && userAgent.equals(
                                        activity.getUserAgent()
                                ));

        boolean newIp = !knownIp;
        boolean newDevice = !knownDevice;

        if (newIp && newDevice) {

            String description =
                    "Login detected from a new IP address "
                    + "and a new device/browser.";

            securityEventService.createEvent(
                    user.getEmail(),
                    SecurityEventType.NEW_IP_AND_DEVICE_LOGIN,
                    description,
                    ipAddress,
                    userAgent
            );

            sendSecurityEmail(
                    user,
                    "New login detected",
                    description,
                    ipAddress,
                    userAgent
            );

        } else if (newIp) {

            String description =
                    "Login detected from a new IP address.";

            securityEventService.createEvent(
                    user.getEmail(),
                    SecurityEventType.NEW_IP_LOGIN,
                    description,
                    ipAddress,
                    userAgent
            );

            sendSecurityEmail(
                    user,
                    "New IP address login detected",
                    description,
                    ipAddress,
                    userAgent
            );

        } else if (newDevice) {

            String description =
                    "Login detected from a new device/browser.";

            securityEventService.createEvent(
                    user.getEmail(),
                    SecurityEventType.NEW_DEVICE_LOGIN,
                    description,
                    ipAddress,
                    userAgent
            );

            sendSecurityEmail(
                    user,
                    "New device login detected",
                    description,
                    ipAddress,
                    userAgent
            );
        }
    }

    private void sendSecurityEmail(
            User user,
            String subject,
            String description,
            String ipAddress,
            String userAgent) {

        try {

            String message =
                    description
                    + "\n\nIP Address: "
                    + ipAddress
                    + "\n\nDevice/Browser: "
                    + userAgent
                    + "\n\nIf this was not you, please change your password.";

            emailService.sendSecurityAlertEmail(
                    user.getEmail(),
                    subject,
                    message
            );

        } catch (Exception e) {

            System.err.println(
                    "Security alert email failed: "
                    + e.getMessage()
            );
        }
    }
}