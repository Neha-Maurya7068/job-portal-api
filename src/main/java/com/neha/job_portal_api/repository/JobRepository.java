package com.neha.job_portal_api.repository;

	import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
	import org.springframework.stereotype.Repository;

	import com.neha.job_portal_api.entity.Job;

	@Repository
	public interface JobRepository extends JpaRepository<Job, Long> {

		List<Job> findByTitleContainingIgnoreCase(String title);
		
		List<Job> findByLocationContainingIgnoreCase(String location);
		
		List<Job> findByJobTypeContainingIgnoreCase(String jobType);
		
		List<Job> findBySalaryGreaterThanEqual(Double salary);
		
		List<Job> findByExperienceLessThanEqual(Integer experience);
		
		List<Job> findByCompanyNameContainingIgnoreCase(String companyName);
		
		List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(
		        String title,
		        String location
		);
		
		List<Job> findBySalaryGreaterThanEqualAndExperienceLessThanEqual(
		        Double salary,
		        Integer experience
		);
		
		List<Job> findByRecruiterId(Long recruiterId);
		
		Optional<Job> findByIdAndRecruiterId(Long jobId, Long recruiterId);
}
