package com.neha.job_portal_api.service;

	import java.util.List;

import com.neha.job_portal_api.dto.JobRequestDTO;
	import com.neha.job_portal_api.dto.JobResponseDTO;

	public interface JobService {

	    JobResponseDTO createJob(JobRequestDTO request);

	    List<JobResponseDTO> getAllJobs();
	}

