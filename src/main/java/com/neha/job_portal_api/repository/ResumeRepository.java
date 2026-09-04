package com.neha.job_portal_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByUserId(Long userId);

    Optional<Resume> findByUserEmail(String email);

    boolean existsByUserId(Long userId);
}