package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.AuditLogDTO;
import com.neha.job_portal_api.entity.AuditLog;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.AuditLogRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.AuditLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl
        implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    private final UserRepository userRepository;

    @Override
    public void saveAuditLog(
            String action,
            String details,
            String ipAddress,
            String httpMethod,
            String requestUri,
            Integer statusCode) {

        try {

            var authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || authentication.getName() == null
                    || authentication.getName().equals("anonymousUser")) {

                return;
            }

            String email = authentication.getName();

            User user =
                    userRepository
                            .findByEmail(email)
                            .orElse(null);

            if (user == null) {
                return;
            }

            AuditLog auditLog =
                    AuditLog.builder()
                            .action(action)
                            .details(details)
                            .ipAddress(ipAddress)
                            .httpMethod(httpMethod)
                            .requestUri(requestUri)
                            .statusCode(statusCode)
                            .createdAt(LocalDateTime.now())
                            .user(user)
                            .build();

            auditLogRepository.save(auditLog);

        } catch (Exception e) {

            System.out.println(
                    "Audit log error: "
                            + e.getMessage());
        }
    }

    @Override
    public List<AuditLogDTO> getAllAuditLogs() {

        return auditLogRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<AuditLogDTO> getMyAuditLogs() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        return auditLogRepository
                .findByUserIdOrderByCreatedAtDesc(
                        user.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private AuditLogDTO mapToDTO(
            AuditLog auditLog) {

        return AuditLogDTO.builder()
                .id(auditLog.getId())
                .userEmail(
                        auditLog.getUser() != null
                                ? auditLog.getUser().getEmail()
                                : null)
                .action(auditLog.getAction())
                .details(auditLog.getDetails())
                .ipAddress(auditLog.getIpAddress())
                .httpMethod(auditLog.getHttpMethod())
                .requestUri(auditLog.getRequestUri())
                .statusCode(auditLog.getStatusCode())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}