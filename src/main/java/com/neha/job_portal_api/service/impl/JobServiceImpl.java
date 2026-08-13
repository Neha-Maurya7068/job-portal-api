package com.neha.job_portal_api.service.impl;

	import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

	import com.neha.job_portal_api.dto.JobRequestDTO;
	import com.neha.job_portal_api.dto.JobResponseDTO;
	import com.neha.job_portal_api.entity.Job;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.JobRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.JobService;

	import lombok.RequiredArgsConstructor;

	@Service
	@RequiredArgsConstructor
	public class JobServiceImpl implements JobService {

		private final JobRepository jobRepository;
		private final UserRepository userRepository;
		
		@Override
		public List<Job> searchJobsByType(String jobType) {
		    return jobRepository.findByJobTypeContainingIgnoreCase(jobType);
		}

		
		@Override
		public List<Job> searchJobsByLocation(String location) {

		    return jobRepository.findByLocationContainingIgnoreCase(location);
		}
		
		@Override
		public List<Job> searchJobsByTitle(String title) {

		    return jobRepository.findByTitleContainingIgnoreCase(title);
		}
		
	    @Override
	    public JobResponseDTO createJob(JobRequestDTO request) {

	        Job job = new Job();
	        
	        String email = SecurityContextHolder
	                .getContext()
	                .getAuthentication()
	                .getName();

	        User recruiter = userRepository.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

	        job.setRecruiter(recruiter);

	        job.setTitle(request.getTitle());
	        job.setCompanyName(request.getCompanyName());
	        job.setLocation(request.getLocation());
	        job.setSalary(request.getSalary());
	        job.setDescription(request.getDescription());
	        job.setJobType(request.getJobType());
	        job.setExperience(request.getExperience());

	        Job savedJob = jobRepository.save(job);

	        return new JobResponseDTO(
	                savedJob.getId(),
	                savedJob.getTitle(),
	                savedJob.getCompanyName(),
	                savedJob.getLocation(),
	                savedJob.getSalary(),
	                savedJob.getDescription(),
	                savedJob.getJobType(),
	                savedJob.getExperience(),
	                savedJob.getCreatedAt()
	        );
	    }
	    @Override
	    public List<JobResponseDTO> getAllJobs() {

	        return jobRepository.findAll()
	                .stream()
	                .map(job -> new JobResponseDTO(
	                        job.getId(),
	                        job.getTitle(),
	                        job.getCompanyName(),
	                        job.getLocation(),
	                        job.getSalary(),
	                        job.getDescription(),
	                        job.getJobType(),
	                        job.getExperience(),
	                        job.getCreatedAt()
	                ))
	                .toList();
	    }
	    
	    @Override
	    public JobResponseDTO getJobById(Long id) {

	        Job job = jobRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Job not found"));

	        return new JobResponseDTO(
	                job.getId(),
	                job.getTitle(),
	                job.getCompanyName(),
	                job.getLocation(),
	                job.getSalary(),
	                job.getDescription(),
	                job.getJobType(),
	                job.getExperience(),
	                job.getCreatedAt()
	        );
	    }
	
	        
	        @Override
	        public JobResponseDTO updateJob(Long id, JobRequestDTO request) {

	            Job job = jobRepository.findById(id)
	                    .orElseThrow(() -> new RuntimeException("Job not found"));

	            job.setTitle(request.getTitle());
	            job.setCompanyName(request.getCompanyName());
	            job.setLocation(request.getLocation());
	            job.setSalary(request.getSalary());
	            job.setDescription(request.getDescription());
	            job.setJobType(request.getJobType());
	            job.setExperience(request.getExperience());

	            Job updatedJob = jobRepository.save(job);

	            return new JobResponseDTO(
	                    updatedJob.getId(),
	                    updatedJob.getTitle(),
	                    updatedJob.getCompanyName(),
	                    updatedJob.getLocation(),
	                    updatedJob.getSalary(),
	                    updatedJob.getDescription(),
	                    updatedJob.getJobType(),
	                    updatedJob.getExperience(),
	                    updatedJob.getCreatedAt()
	            );
	            
	        }
	            
	            @Override
	            public void deleteJob(Long id) {

	                Job job = jobRepository.findById(id)
	                        .orElseThrow(() -> new RuntimeException("Job not found"));

	                jobRepository.delete(job);
	            }
	    
	    
	}


