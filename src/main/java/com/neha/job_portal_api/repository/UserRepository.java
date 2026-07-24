package com.neha.job_portal_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

    boolean existsByEmail(String email);
	
}
