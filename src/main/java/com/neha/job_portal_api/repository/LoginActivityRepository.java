package com.neha.job_portal_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neha.job_portal_api.entity.LoginActivity;

public interface LoginActivityRepository
        extends JpaRepository<LoginActivity, Long> {

    Optional<LoginActivity> findByTokenId(String tokenId);

    Optional<LoginActivity> findByTokenIdAndActiveTrue(String tokenId);

    List<LoginActivity> findByUserIdOrderByLoginAtDesc(Long userId);

    Optional<LoginActivity> findByIdAndUserId(Long id, Long userId);

    void deleteByUserId(Long userId);
    
    long countByActiveTrue();
    
}