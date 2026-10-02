package com.neha.job_portal_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neha.job_portal_api.entity.Interview;
import com.neha.job_portal_api.entity.InterviewStatus;

public interface InterviewRepository
extends JpaRepository<Interview, Long> {

List<Interview>
findByApplicationIdOrderByInterviewDateTimeAsc(
    Long applicationId);

List<Interview>
findByCreatedByIdOrderByInterviewDateTimeAsc(
    Long recruiterId);

List<Interview>
findByApplicationUserIdOrderByInterviewDateTimeAsc(
    Long userId);

long countByCreatedByIdAndStatus(
        Long recruiterId,
        InterviewStatus status);

List<Interview> findByCreatedByIdAndStatusOrderByInterviewDateTimeAsc(
        Long recruiterId,
        InterviewStatus status);

List<Interview> findByCreatedByIdAndStatus(
        Long createdById,
        InterviewStatus status
);

@Query("""
	    SELECT COUNT(i) > 0
	    FROM Interview i
	    WHERE i.createdBy.id = :recruiterId
	    AND i.status <> com.neha.job_portal_api.entity.InterviewStatus.CANCELLED
	    AND i.interviewDateTime < :endTime
	    AND i.slot.endTime > :startTime
	    AND (:interviewId IS NULL OR i.id <> :interviewId)
	    """)
	boolean existsInterviewConflict(
	        @Param("recruiterId") Long recruiterId,
	        @Param("startTime") LocalDateTime startTime,
	        @Param("endTime") LocalDateTime endTime,
	        @Param("interviewId") Long interviewId);

@Query("""
	    SELECT i FROM Interview i
	    WHERE i.createdBy.id = :recruiterId
	    AND i.interviewDateTime < :endTime
	    AND i.interviewDateTime >= :startTime
	    ORDER BY i.interviewDateTime ASC
	    """)
	List<Interview> findInterviewsForCalendar(
	        @Param("recruiterId") Long recruiterId,
	        @Param("startTime") LocalDateTime startTime,
	        @Param("endTime") LocalDateTime endTime
	);

@Query("""
        SELECT i FROM Interview i
        WHERE i.application.user.id = :userId
        AND i.interviewDateTime >= :startTime
        AND i.interviewDateTime < :endTime
        ORDER BY i.interviewDateTime ASC
        """)
List<Interview> findCandidateCalendarInterviews(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
}