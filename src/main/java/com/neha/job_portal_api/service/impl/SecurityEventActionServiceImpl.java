package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.job_portal_api.dto.SecurityEventActionDTO;
import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.entity.SecurityEvent;
import com.neha.job_portal_api.entity.SecurityEventAction;
import com.neha.job_portal_api.entity.SecurityEventActionType;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.SecurityEventActionRepository;
import com.neha.job_portal_api.repository.SecurityEventRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.SecurityEventActionService;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor

public class SecurityEventActionServiceImpl
        implements SecurityEventActionService {

    private final SecurityEventActionRepository
            securityEventActionRepository;

    private final SecurityEventRepository
            securityEventRepository;

    private final UserRepository userRepository;
    
    private final SecurityEventActionService
    securityEventActionService;

    @Override
    public List<SecurityEventActionDTO> getHistory(
            Long securityEventId) {

        if (!securityEventRepository.existsById(securityEventId)) {
            throw new RuntimeException(
                    "Security event not found with id: "
                            + securityEventId
            );
        }

        return securityEventActionRepository
                .findBySecurityEventIdOrderByPerformedAtDesc(
                        securityEventId
                )
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public SecurityEventActionDTO addAction(
            Long securityEventId,
            SecurityEventActionType action,
            String note) {

        SecurityEvent event =
                securityEventRepository
                        .findById(securityEventId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Security event not found"
                                )
                        );

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User admin =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Admin user not found"
                                )
                        );

        SecurityEventAction eventAction =
                SecurityEventAction.builder()
                        .securityEvent(event)
                        .action(action)
                        .performedBy(admin)
                        .note(note)
                        .performedAt(
                                LocalDateTime.now()
                        )
                        .build();

        SecurityEventAction saved =
                securityEventActionRepository
                        .save(eventAction);

        return mapToDTO(saved);
    }

    private SecurityEventActionDTO mapToDTO(
            SecurityEventAction action) {

        return SecurityEventActionDTO.builder()
                .id(action.getId())
                .securityEventId(
                        action.getSecurityEvent().getId()
                )
                .action(action.getAction())
                .performedBy(
                        action.getPerformedBy().getEmail()
                )
                .note(action.getNote())
                .performedAt(action.getPerformedAt())
                .build();
    }
    
    private SecurityEventDTO mapEventToDTO(
            SecurityEvent event) {

        return SecurityEventDTO.builder()
                // SecurityEventDTO ke actual fields yahan map honge
                .build();
    }
    
    @Override
    public SecurityEventDTO resolveEvent(Long id) {

        SecurityEvent event =
                securityEventRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Security event not found"
                                )
                        );

        if (event.isResolved()) {
            throw new RuntimeException(
                    "Security event is already resolved"
            );
        }

        String adminEmail =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User admin =
                userRepository
                        .findByEmail(adminEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Admin user not found"
                                )
                        );

        event.setResolved(true);
        event.setResolvedAt(LocalDateTime.now());
        event.setResolvedBy(admin);

        SecurityEvent savedEvent =
                securityEventRepository.save(event);

        securityEventActionService.addAction(
                id,
                SecurityEventActionType.RESOLVED,
                "Security event resolved by admin"
        );

        return mapEventToDTO(savedEvent);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SecurityEventActionDTO> getInvestigationNotes(
            Long eventId) {

        if (!securityEventRepository.existsById(eventId)) {
            throw new RuntimeException(
                    "Security event not found with id: " + eventId
            );
        }

        return securityEventActionRepository
                .findBySecurityEventIdAndActionOrderByPerformedAtDesc(
                        eventId,
                        SecurityEventActionType.INVESTIGATED
                )
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    
    @Override
    @Transactional
    public SecurityEventActionDTO addInvestigationNote(
            Long eventId,
            String note) {

        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException(
                    "Investigation note is required"
            );
        }

        return addAction(
                eventId,
                SecurityEventActionType.INVESTIGATED,
                note.trim()
        );
    }
    
}