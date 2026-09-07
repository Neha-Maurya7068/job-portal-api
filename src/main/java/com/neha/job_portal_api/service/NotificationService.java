package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.NotificationDTO;

public interface NotificationService {

    List<NotificationDTO> getMyNotifications();

    List<NotificationDTO> getUnreadNotifications();

    void markAsRead(Long notificationId);

    void markAllAsRead();

    void deleteNotification(Long notificationId);
}