package com.neha.job_portal_api.entity;

	import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
	import jakarta.persistence.GenerationType;
	import jakarta.persistence.Id;
	import jakarta.persistence.Table;

	import lombok.AllArgsConstructor;
	import lombok.Data;
	import lombok.NoArgsConstructor;

	@Entity
	@Table(name = "users")
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class User {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    
	    private Long id;

	    @Column(nullable = false)

	    private String name;

	    @Column(unique = true, nullable = false)
	    
	    private String email;

	    @Column(nullable = false)

	    private String password;
	    
	    @Enumerated(EnumType.STRING)
	    private Role role;

	}

