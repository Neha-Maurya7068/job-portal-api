package com.neha.job_portal_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neha.job_portal_api.entity.InterviewSlot;

public interface InterviewSlotRepository
        extends JpaRepository<InterviewSlot, Long> {

    List<InterviewSlot> findByRecruiterIdAndAvailableTrueOrderByStartTimeAsc(
            Long recruiterId);

    List<InterviewSlot> findByRecruiterIdOrderByStartTimeAsc(
            Long recruiterId);
    
    Optional<InterviewSlot> findByIdAndRecruiterId(
            Long slotId,
            Long recruiterId);

    boolean existsByRecruiterIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long recruiterId,
            LocalDateTime endTime,
            LocalDateTime startTime);
    
    @Query("""
    	    SELECT s FROM InterviewSlot s
    	    WHERE s.recruiter.id = :recruiterId
    	    AND s.startTime < :endTime
    	    AND s.endTime > :startTime
    	    ORDER BY s.startTime ASC
    	    """)
    	List<InterviewSlot> findSlotsForCalendar(
    	        @Param("recruiterId") Long recruiterId,
    	        @Param("startTime") LocalDateTime startTime,
    	        @Param("endTime") LocalDateTime endTime
    	);
    
   
}