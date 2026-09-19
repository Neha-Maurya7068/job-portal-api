package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.JobAlertRequestDTO;
import com.neha.job_portal_api.dto.JobAlertResponseDTO;
import com.neha.job_portal_api.entity.JobAlert;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.JobAlertRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.JobAlertService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobAlertServiceImpl implements JobAlertService {

    private final JobAlertRepository jobAlertRepository;
    private final UserRepository userRepository;

    @Override
    public JobAlertResponseDTO createAlert(JobAlertRequestDTO request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        JobAlert alert = new JobAlert();

        alert.setTitle(request.getTitle());
        alert.setLocation(request.getLocation());
        alert.setJobType(request.getJobType());
        alert.setMinSalary(request.getMinSalary());
        alert.setMinExperience(request.getMinExperience());
        alert.setActive(true);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setUser(user);

        JobAlert savedAlert = jobAlertRepository.save(alert);

        return mapToDTO(savedAlert);
    }

    @Override
    public List<JobAlertResponseDTO> getMyAlerts() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return jobAlertRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public void deactivateAlert(Long alertId) {

        JobAlert alert = getMyAlert(alertId);

        alert.setActive(false);

        jobAlertRepository.save(alert);
    }

    @Override
    public void deleteAlert(Long alertId) {

        JobAlert alert = getMyAlert(alertId);

        jobAlertRepository.delete(alert);
    }

    private JobAlert getMyAlert(Long alertId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return jobAlertRepository
                .findById(alertId)
                .filter(alert ->
                        alert.getUser().getId().equals(user.getId()))
                .orElseThrow(() ->
                        new RuntimeException("Job alert not found"));
    }

    private JobAlertResponseDTO mapToDTO(JobAlert alert) {

        return new JobAlertResponseDTO(
                alert.getId(),
                alert.getTitle(),
                alert.getLocation(),
                alert.getJobType(),
                alert.getMinSalary(),
                alert.getMinExperience(),
                alert.isActive(),
                alert.getCreatedAt()
        );
    }
}