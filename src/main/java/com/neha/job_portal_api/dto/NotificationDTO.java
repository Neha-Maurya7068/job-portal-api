package com.neha.job_portal_api.dto;

import java.time.LocalDateTime;

import com.neha.job_portal_api.entity.NotificationType;
import com.neha.job_portal_api.entity.User;

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

    private LocalDateTime createdAt;
    
    private User user;
    
    private String title;
    
    private boolean isRead;
    }