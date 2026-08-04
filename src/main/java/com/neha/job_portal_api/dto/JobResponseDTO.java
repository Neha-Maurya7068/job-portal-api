package com.neha.job_portal_api.dto;

	import java.time.LocalDateTime;
	import lombok.AllArgsConstructor;
	import lombok.Data;
	import lombok.NoArgsConstructor;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class JobResponseDTO {

	    private Long id;
	    private String title;
	    private String companyName;
	    private String location;
	    private Double salary;
	    private String description;
	    private String jobType;
	    private Integer experience;
	    private LocalDateTime createdAt;
	}
	

