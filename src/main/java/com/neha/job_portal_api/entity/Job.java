package com.neha.job_portal_api.entity;	

	import java.time.LocalDateTime;
	import jakarta.persistence.*;
	import lombok.AllArgsConstructor;
	import lombok.Data;
	import lombok.NoArgsConstructor;

	@Entity
	@Table(name = "jobs")
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class Job {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String title;

	    private String companyName;

	    private String location;

	    private Double salary;

	    @Column(length = 2000)
	    private String description;

	    private String jobType;

	    private Integer experience;

	    private LocalDateTime createdAt;
	    
	    @PrePersist
	    public void onCreate() {
	        this.createdAt = LocalDateTime.now();
	    }
	}
	
