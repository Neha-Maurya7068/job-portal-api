package com.neha.job_portal_api.service;

	import java.util.List;
    import com.neha.job_portal_api.dto.JobRequestDTO;
	import com.neha.job_portal_api.dto.JobResponseDTO;
    import com.neha.job_portal_api.entity.Job;

	public interface JobService {

	    JobResponseDTO createJob(JobRequestDTO request);

	    List<JobResponseDTO> getAllJobs();
	    
	    JobResponseDTO getJobById(Long id);
	    
	    JobResponseDTO updateJob(Long id, JobRequestDTO request);
	    
	    List<Job> searchJobsByTitle(String title);
	    
	    List<Job> searchJobsByLocation(String location);
	    
	    List<Job> searchJobsByType(String jobType);
	    
	    void deleteJob(Long id);

	}

