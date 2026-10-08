package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.entity.SecurityEvent;
import com.neha.job_portal_api.entity.SecurityEventType;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.SecurityEventRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.SecurityEventService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityEventServiceImpl
        implements SecurityEventService {

    private final SecurityEventRepository securityEventRepository;
    private final UserRepository userRepository;

    @Override
    public void createEvent(
            String email,
            SecurityEventType type,
            String description,
            String ipAddress,
            String userAgent) {

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        SecurityEvent event = SecurityEvent.builder()
                .email(email)
                .user(user)
                .type(type)
                .description(description)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .resolved(false)
                .build();

        securityEventRepository.save(event);
    }

    @Override
    public List<SecurityEventDTO> getAllEvents() {

        return securityEventRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<SecurityEventDTO> getMyEvents() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        return securityEventRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private SecurityEventDTO mapToDTO(SecurityEvent event) {

        return SecurityEventDTO.builder()
                .id(event.getId())
                .userEmail(event.getEmail())
                .type(event.getType())
                .description(event.getDescription())
                .ipAddress(event.getIpAddress())
                .userAgent(event.getUserAgent())
                .createdAt(event.getCreatedAt())
                .resolved(event.isResolved())
                .build();
    }
    
    @Override
    public List<SecurityEventDTO> getEventsByResolved(boolean resolved) {

        return securityEventRepository
                .findByResolvedOrderByCreatedAtDesc(resolved)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    
    @Override
    public SecurityEventDTO getEventById(Long id) {

        SecurityEvent event = securityEventRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Security event not found with id: " + id
                        )
                );

        return mapToDTO(event);
    }
    
    @Override
    public SecurityEventDTO resolveEvent(Long id) {

        SecurityEvent event = securityEventRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Security event not found with id: " + id
                        )
                );

        if (event.isResolved()) {
            throw new RuntimeException(
                    "Security event is already resolved"
            );
        }

        event.setResolved(true);

        SecurityEvent savedEvent =
                securityEventRepository.save(event);

        return mapToDTO(savedEvent);
    }
}