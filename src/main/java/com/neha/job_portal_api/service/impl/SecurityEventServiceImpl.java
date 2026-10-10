package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.job_portal_api.dto.SecurityEventDTO;
import com.neha.job_portal_api.entity.Role;
import com.neha.job_portal_api.entity.SecurityEvent;
import com.neha.job_portal_api.entity.SecurityEventActionType;
import com.neha.job_portal_api.entity.SecurityEventType;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.SecurityEventRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.SecurityEventActionService;
import com.neha.job_portal_api.service.SecurityEventService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityEventServiceImpl
        implements SecurityEventService {

    private final SecurityEventRepository securityEventRepository;
    private final UserRepository userRepository;
    private final SecurityEventActionService securityEventActionService;

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


private SecurityEventDTO mapToDTO(
        SecurityEvent event) {

    return SecurityEventDTO.builder()
            .id(event.getId())
            .userEmail(event.getEmail())
            .type(event.getType())
            .description(event.getDescription())
            .ipAddress(event.getIpAddress())
            .userAgent(event.getUserAgent())
            .createdAt(event.getCreatedAt())
            .resolved(event.isResolved())
            .resolvedAt(event.getResolvedAt())
            .resolvedBy(
                    event.getResolvedBy() != null
                            ? event.getResolvedBy().getEmail()
                            : null
            )
            .assignedToUserId(
                    event.getAssignedTo() != null
                            ? event.getAssignedTo().getId()
                            : null
            )
            .assignedToEmail(
                    event.getAssignedTo() != null
                            ? event.getAssignedTo().getEmail()
                            : null
            )
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

        String adminEmail =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User admin = userRepository
                .findByEmail(adminEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Admin user not found"
                        )
                );

        event.setResolved(true);

        event.setResolvedAt(
                LocalDateTime.now()
        );

        event.setResolvedBy(admin);

        SecurityEvent savedEvent =
                securityEventRepository.save(event);

        return mapToDTO(savedEvent);
    }
    
    @Override
    @Transactional
    public SecurityEventDTO reopenEvent(Long id, String note) {

        SecurityEvent event = securityEventRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Security event not found with id: " + id
                        )
                );

        if (!event.isResolved()) {
            throw new RuntimeException(
                    "Only resolved security events can be reopened"
            );
        }

        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException(
                    "Reopen reason is required"
            );
        }

        // Record the action before changing the current event state.
        // The action service records the current authenticated admin.
        securityEventActionService.addAction(
                id,
                SecurityEventActionType.REOPENED,
                note.trim()
        );

        // Reopen the event
        event.setResolved(false);
        event.setResolvedAt(null);
        event.setResolvedBy(null);

        SecurityEvent savedEvent =
                securityEventRepository.save(event);

        return mapToDTO(savedEvent);
    }
    
    @Override
    @Transactional
    public SecurityEventDTO assignEvent(
            Long eventId,
            Long assignedToUserId) {

        SecurityEvent event = securityEventRepository
                .findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Security event not found: " + eventId
                        )
                );

        if (event.isResolved()) {
            throw new IllegalStateException(
                    "Resolved security events cannot be assigned"
            );
        }

        User targetUser = userRepository
                .findById(assignedToUserId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Assigned user not found: " + assignedToUserId
                        )
                );

        if (targetUser.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException(
                    "Security events can only be assigned to an ADMIN user"
            );
        }

        User previousAssignee = event.getAssignedTo();

        if (previousAssignee != null
                && previousAssignee.getId().equals(targetUser.getId())) {
            throw new IllegalStateException(
                    "Security event is already assigned to this admin"
            );
        }

        SecurityEventActionType actionType =
                previousAssignee == null
                        ? SecurityEventActionType.ASSIGNED
                        : SecurityEventActionType.REASSIGNED;

        String note = previousAssignee == null
                ? "Assigned to " + targetUser.getEmail()
                : "Reassigned from " + previousAssignee.getEmail()
                        + " to " + targetUser.getEmail();

        event.setAssignedTo(targetUser);

        SecurityEvent savedEvent =
                securityEventRepository.save(event);

        securityEventActionService.addAction(
                eventId,
                actionType,
                note
        );

        return mapToDTO(savedEvent);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SecurityEventDTO> getMyAssignedEvents() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Logged-in admin not found"));

        return securityEventRepository
                .findByAssignedToIdOrderByCreatedAtDesc(admin.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SecurityEventDTO> getMyAssignedEvents(
            String status, int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Logged-in admin not found"));

        Pageable pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<SecurityEvent> events;

        if (status == null || status.isBlank()) {
            events = securityEventRepository
                    .findByAssignedToIdOrderByCreatedAtDesc(
                            admin.getId(), pageable);
        } else {
            boolean resolved;

            if ("OPEN".equalsIgnoreCase(status)) {
                resolved = false;
            } else if ("RESOLVED".equalsIgnoreCase(status)) {
                resolved = true;
            } else {
                throw new IllegalArgumentException(
                        "Invalid status. Use OPEN or RESOLVED");
            }

            events = securityEventRepository
                    .findByAssignedToIdAndResolvedOrderByCreatedAtDesc(
                            admin.getId(), resolved, pageable);
        }

        return events.map(this::mapToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SecurityEventDTO> getMyAssignedEvents(
            String status,
            String sortBy,
            String direction,
            int page,
            int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Logged-in admin not found"));

        Boolean resolved = null;

        if (status != null && !status.isBlank()) {
            if ("OPEN".equalsIgnoreCase(status)) {
                resolved = false;
            } else if ("RESOLVED".equalsIgnoreCase(status)) {
                resolved = true;
            } else {
                throw new IllegalArgumentException(
                        "Invalid status. Use OPEN or RESOLVED");
            }
        }

        String normalizedSort =
                sortBy == null ? "PRIORITY" : sortBy.toUpperCase();

        String normalizedDirection =
                direction == null ? "DESC" : direction.toUpperCase();

        if (!normalizedSort.equals("PRIORITY")
                && !normalizedSort.equals("DATE")) {
            throw new IllegalArgumentException(
                    "Invalid sortBy. Use PRIORITY or DATE");
        }

        if (!normalizedDirection.equals("ASC")
                && !normalizedDirection.equals("DESC")) {
            throw new IllegalArgumentException(
                    "Invalid direction. Use ASC or DESC");
        }

        Page<SecurityEvent> events;

        if ("PRIORITY".equals(normalizedSort)) {

            Pageable pageable = PageRequest.of(page, size);

            events = securityEventRepository
                    .findAssignedEventsByPriority(
                            admin.getId(), resolved, pageable);

        } else {

            Sort sort = Sort.by(
                    Sort.Direction.valueOf(normalizedDirection),
                    "createdAt");

            Pageable pageable = PageRequest.of(page, size, sort);

            if (resolved == null) {
                events = securityEventRepository
                        .findByAssignedToId(admin.getId(), pageable);
            } else {
                events = securityEventRepository
                        .findByAssignedToIdAndResolved(
                                admin.getId(), resolved, pageable);
            }
        }

        return events.map(this::mapToDTO);
    }
}