package com.neha.job_portal_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appliedAt;
    
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    
    private LocalDateTime statusUpdatedAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private Job job;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
    
    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }
    
}