package com.neha.job_portal_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neha.job_portal_api.entity.JobAlert;

public interface JobAlertRepository
        extends JpaRepository<JobAlert, Long> {

    List<JobAlert> findByUserId(Long userId);

    List<JobAlert> findByActiveTrue();

    @Query("""
        SELECT a FROM JobAlert a
        WHERE a.active = true
        AND (:title IS NULL OR
             LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:location IS NULL OR
             LOWER(a.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:jobType IS NULL OR
             LOWER(a.jobType) = LOWER(:jobType))
        AND (:salary IS NULL OR
             a.minSalary IS NULL OR
             :salary >= a.minSalary)
        AND (:experience IS NULL OR
             a.minExperience IS NULL OR
             :experience >= a.minExperience)
        """)
    List<JobAlert> findMatchingAlerts(
            @Param("title") String title,
            @Param("location") String location,
            @Param("jobType") String jobType,
            @Param("salary") Double salary,
            @Param("experience") Integer experience);
}