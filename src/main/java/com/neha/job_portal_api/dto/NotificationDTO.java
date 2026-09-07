package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import com.neha.job_portal_api.entity.NotificationType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private Long id;

    private String message;

    private NotificationType type;

    private boolean isRead;

    private LocalDateTime createdAt;
}