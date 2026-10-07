package com.neha.job_portal_api.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.neha.job_portal_api.service.AuditLogService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditLogFilter
        extends OncePerRequestFilter {

    private final AuditLogService auditLogService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();

        String uri = request.getRequestURI();

        // Sirf important modification requests audit honge
        boolean shouldAudit =
                method.equalsIgnoreCase("POST")
                || method.equalsIgnoreCase("PUT")
                || method.equalsIgnoreCase("PATCH")
                || method.equalsIgnoreCase("DELETE");

        // Audit API ko khud audit nahi karna
        boolean isAuditEndpoint =
                uri.startsWith("/api/audit-logs");

        if (!shouldAudit || isAuditEndpoint) {

            filterChain.doFilter(
                    request,
                    response);

            return;
        }

        try {

            filterChain.doFilter(
                    request,
                    response);

        } finally {

            String action =
                    getActionFromMethod(method);

            String ipAddress =
                    getClientIpAddress(request);

            String details =
                    method + " request performed on "
                            + uri;

            auditLogService.saveAuditLog(
                    action,
                    details,
                    ipAddress,
                    method,
                    uri,
                    response.getStatus());
        }
    }

    private String getActionFromMethod(
            String method) {

        return switch (method.toUpperCase()) {

            case "POST" -> "CREATE";

            case "PUT", "PATCH" -> "UPDATE";

            case "DELETE" -> "DELETE";

            default -> "OTHER";
        };
    }

    private String getClientIpAddress(
            HttpServletRequest request) {

        String ip =
                request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}