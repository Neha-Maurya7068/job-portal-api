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
	    
	    List<Job> searchJobsBySalary(Double salary);

	    List<Job> searchJobsByExperience(Integer experience);
	    
	    List<Job> searchJobsByCompanyName(String companyName);
	    
	    List<Job> searchJobsByTitleAndLocation(String title, String location);
	    
	    List<Job> searchJobsBySalaryAndExperience(
	            Double salary,
	            Integer experience
	    );
	    
	    void deleteJob(Long id);

	}

