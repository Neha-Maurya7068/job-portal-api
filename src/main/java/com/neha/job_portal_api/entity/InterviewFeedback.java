package com.neha.job_portal_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;

    @Column(length = 2000)
    private String technicalSkills;

    @Column(length = 2000)
    private String strengths;

    @Column(length = 2000)
    private String weaknesses;

    @Column(length = 2000)
    private String comments;

    @Enumerated(EnumType.STRING)
    private FeedbackRecommendation recommendation;

    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
} 