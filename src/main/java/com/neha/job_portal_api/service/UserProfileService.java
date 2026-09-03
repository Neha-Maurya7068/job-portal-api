package com.neha.job_portal_api.service;

import com.neha.job_portal_api.dto.UserProfileDTO;

public interface UserProfileService {

    UserProfileDTO getMyProfile();

    UserProfileDTO updateMyProfile(UserProfileDTO request);
}