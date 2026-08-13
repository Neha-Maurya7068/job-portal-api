package com.neha.job_portal_api.repository;

	import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
	import org.springframework.stereotype.Repository;

	import com.neha.job_portal_api.entity.Job;

	@Repository
	public interface JobRepository extends JpaRepository<Job, Long> {

		List<Job> findByTitleContainingIgnoreCase(String title);
		
		List<Job> findByLocationContainingIgnoreCase(String location);
	
}
