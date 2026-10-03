package com.neha.job_portal_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean emailEnabled = true;

    private boolean inAppEnabled = true;

    private boolean jobAlertEnabled = true;

    private boolean interviewReminderEnabled = true;

    private boolean applicationStatusEnabled = true;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}