package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.NotificationDTO;
import com.neha.job_portal_api.entity.NotificationType;
import com.neha.job_portal_api.entity.User;

public interface NotificationService {

    NotificationDTO createNotification(
            User user,
            String message,
            NotificationType type
    );

    List<NotificationDTO> getMyNotifications();

    List<NotificationDTO> getUnreadNotifications();

    void markAsRead(Long notificationId);

    void markAllAsRead();

    void deleteNotification(Long notificationId);
}