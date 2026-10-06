package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.LoginActivityDTO;
import com.neha.job_portal_api.entity.LoginActivity;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.LoginActivityRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.LoginActivityService;
import com.neha.job_portal_api.service.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginActivityServiceImpl
        implements LoginActivityService {

    private final LoginActivityRepository loginActivityRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void recordLogin(
            User user,
            String token,
            String ipAddress,
            String userAgent) {

        String tokenId = jwtService.extractTokenId(token);

        LocalDateTime expiresAt =
                jwtService.extractExpiration(token)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

        LoginActivity activity = new LoginActivity();

        activity.setTokenId(tokenId);
        activity.setIpAddress(ipAddress);
        activity.setUserAgent(userAgent);
        activity.setLoginAt(LocalDateTime.now());
        activity.setExpiresAt(expiresAt);
        activity.setActive(true);
        activity.setUser(user);

        loginActivityRepository.save(activity);
    }

    @Override
    public List<LoginActivityDTO> getMyLoginActivities() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return loginActivityRepository
                .findByUserIdOrderByLoginAtDesc(user.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public void logoutCurrentSession(String token) {

        String tokenId = jwtService.extractTokenId(token);

        LoginActivity activity =
                loginActivityRepository
                        .findByTokenId(tokenId)
                        .orElseThrow(() ->
                                new RuntimeException("Session not found"));

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (!activity.getUser().getEmail().equals(email)) {
            throw new RuntimeException(
                    "You can logout only your own session");
        }

        activity.setActive(false);
        activity.setLogoutAt(LocalDateTime.now());

        loginActivityRepository.save(activity);
    }

    @Override
    public void logoutAllSessions() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<LoginActivity> activities =
                loginActivityRepository
                        .findByUserIdOrderByLoginAtDesc(user.getId());

        LocalDateTime now = LocalDateTime.now();

        activities.forEach(activity -> {

            if (activity.isActive()) {
                activity.setActive(false);
                activity.setLogoutAt(now);
            }
        });

        loginActivityRepository.saveAll(activities);
    }

    private LoginActivityDTO mapToDTO(
            LoginActivity activity) {

        return LoginActivityDTO.builder()
                .id(activity.getId())
                .ipAddress(activity.getIpAddress())
                .userAgent(activity.getUserAgent())
                .loginAt(activity.getLoginAt())
                .logoutAt(activity.getLogoutAt())
                .expiresAt(activity.getExpiresAt())
                .active(activity.isActive())
                .build();
    }
}