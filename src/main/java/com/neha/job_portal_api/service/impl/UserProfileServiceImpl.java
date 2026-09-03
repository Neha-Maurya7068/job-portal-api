package com.neha.job_portal_api.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.UserProfileDTO;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.exception.ResourceNotFoundException;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;

    @Override
    public UserProfileDTO getMyProfile() {

        User user = getLoggedInUser();

        return convertToDTO(user);
    }

    @Override
    public UserProfileDTO updateMyProfile(
            UserProfileDTO request) {

        User user = getLoggedInUser();

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setLocation(request.getLocation());
        user.setSkills(request.getSkills());
        user.setEducation(request.getEducation());
        user.setExperience(request.getExperience());

        User updatedUser = userRepository.save(user);

        return convertToDTO(updatedUser);
    }

    // ================= LOGGED IN USER =================

    private User getLoggedInUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    // ================= ENTITY TO DTO =================

    private UserProfileDTO convertToDTO(User user) {

        return new UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getLocation(),
                user.getSkills(),
                user.getEducation(),
                user.getExperience()
        );
    }
}