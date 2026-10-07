package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.AuditLogDTO;

public interface AuditLogService {

    void saveAuditLog(
            String action,
            String details,
            String ipAddress,
            String httpMethod,
            String requestUri,
            Integer statusCode);

    List<AuditLogDTO> getAllAuditLogs();

    List<AuditLogDTO> getMyAuditLogs();
}