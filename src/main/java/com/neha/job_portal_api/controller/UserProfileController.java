package com.neha.job_portal_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.dto.ApiResponse;
import com.neha.job_portal_api.dto.UserProfileDTO;
import com.neha.job_portal_api.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // ================= GET PROFILE =================

    @GetMapping
    public ApiResponse<UserProfileDTO> getMyProfile() {

        UserProfileDTO profile =
                userProfileService.getMyProfile();

        return new ApiResponse<>(
                true,
                "Profile fetched successfully",
                profile
        );
    }

    // ================= UPDATE PROFILE =================

    @PutMapping
    public ApiResponse<UserProfileDTO> updateMyProfile(
            @RequestBody UserProfileDTO request) {

        UserProfileDTO profile =
                userProfileService.updateMyProfile(request);

        return new ApiResponse<>(
                true,
                "Profile updated successfully",
                profile
        );
    }
}