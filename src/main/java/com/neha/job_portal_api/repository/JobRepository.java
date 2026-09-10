package com.neha.job_portal_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neha.job_portal_api.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findByJobTypeContainingIgnoreCase(String jobType);

    List<Job> findBySalaryGreaterThanEqual(Double salary);

    List<Job> findByExperienceLessThanEqual(Integer experience);

    List<Job> findByCompanyNameContainingIgnoreCase(String companyName);

    List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(
            String title, String location);

    List<Job> findBySalaryGreaterThanEqualAndExperienceLessThanEqual(
            Double salary, Integer experience);

    Page<Job> findAll(Pageable pageable);

    List<Job> findByRecruiterId(Long recruiterId);

    Page<Job> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Job> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    Page<Job> findByJobTypeContainingIgnoreCase(String jobType, Pageable pageable);

    Page<Job> findBySalaryGreaterThanEqual(Double salary, Pageable pageable);

    Optional<Job> findByIdAndRecruiterId(Long jobId, Long recruiterId);


    // Advanced Job Search
    @Query("""
            SELECT j FROM Job j
            WHERE (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%')))
            AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
            AND (:companyName IS NULL OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :companyName, '%')))
            AND (:minSalary IS NULL OR j.salary >= :minSalary)
            AND (:maxSalary IS NULL OR j.salary <= :maxSalary)
            AND (:minExperience IS NULL OR j.experience >= :minExperience)
            AND (:maxExperience IS NULL OR j.experience <= :maxExperience)
            AND (:jobType IS NULL OR LOWER(j.jobType) = LOWER(:jobType))
            """)
    Page<Job> searchJobs(
            @Param("title") String title,
            @Param("location") String location,
            @Param("companyName") String companyName,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            @Param("minExperience") Integer minExperience,
            @Param("maxExperience") Integer maxExperience,
            @Param("jobType") String jobType,
            Pageable pageable);
}