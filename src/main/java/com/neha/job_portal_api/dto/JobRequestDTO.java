package com.neha.job_portal_api.dto;
	import jakarta.validation.constraints.Min;
	import jakarta.validation.constraints.NotBlank;
	import jakarta.validation.constraints.NotNull;
	import lombok.AllArgsConstructor;
	import lombok.Data;
	import lombok.NoArgsConstructor;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class JobRequestDTO {

	    @NotBlank(message = "Job title is required")
	    private String title;

	    @NotBlank(message = "Company name is required")
	    private String companyName;

	    @NotBlank(message = "Location is required")
	    private String location;

	    @NotNull(message = "Salary is required")
	    @Min(value = 0, message = "Salary must be greater than or equal to 0")
	    private Double salary;

	    @NotBlank(message = "Description is required")
	    private String description;

	    @NotBlank(message = "Job type is required")
	    private String jobType;

	    @NotNull(message = "Experience is required")
	    @Min(value = 0, message = "Experience cannot be negative")
	    private Integer experience;
	}
	

