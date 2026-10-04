package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.NotificationDTO;
import com.neha.job_portal_api.entity.Notification;
import com.neha.job_portal_api.entity.NotificationPreference;
import com.neha.job_portal_api.entity.NotificationType;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.NotificationPreferenceRepository;
import com.neha.job_portal_api.repository.NotificationRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    @Override
    public List<NotificationDTO> getMyNotifications() {

        User user = getCurrentUser();

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<NotificationDTO> getUnreadNotifications() {

        User user = getCurrentUser();

        return notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public void markAsRead(Long notificationId) {

        User user = getCurrentUser();

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not authorized to update this notification");
        }

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {

        User user = getCurrentUser();

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(
                                user.getId());

        notifications.forEach(
                notification -> notification.setRead(true));

        notificationRepository.saveAll(notifications);
    }

    @Override
    public void deleteNotification(Long notificationId) {

        User user = getCurrentUser();

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not authorized to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private NotificationDTO convertToDTO(
            Notification notification) {

        return NotificationDTO.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    @Override
    public NotificationDTO createNotification(
            User user,
            String message,
            NotificationType type) {

        NotificationPreference preference =
                preferenceRepository
                        .findByUserId(user.getId())
                        .orElseGet(() -> createDefaultPreference(user));

        // In-app notification OFF hai
        if (!preference.isInAppEnabled()) {
            return null;
        }

        // Specific notification type ki preference check
        if (type == NotificationType.NEW_JOB
                && !preference.isJobAlertEnabled()) {
            return null;
        }

        if (type == NotificationType.INTERVIEW_SCHEDULED
                && !preference.isInterviewReminderEnabled()) {
            return null;
        }

        if ((type == NotificationType.APPLICATION_SUBMITTED
                || type == NotificationType.APPLICATION_STATUS_UPDATED)
                && !preference.isApplicationStatusEnabled()) {
            return null;
        }

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification =
                notificationRepository.save(notification);

        return convertToDTO(savedNotification);
    }

    private NotificationPreference createDefaultPreference(
            User user) {

        NotificationPreference preference =
                new NotificationPreference();

        preference.setEmailEnabled(true);
        preference.setInAppEnabled(true);
        preference.setJobAlertEnabled(true);
        preference.setInterviewReminderEnabled(true);
        preference.setApplicationStatusEnabled(true);
        preference.setUser(user);

        return preferenceRepository.save(preference);
    }
}