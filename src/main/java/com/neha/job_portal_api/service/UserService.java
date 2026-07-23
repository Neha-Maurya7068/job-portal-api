package com.neha.job_portal_api.service;

import com.neha.job_portal_api.dto.RegisterRequestDTO;

public interface UserService {

    String registerUser(RegisterRequestDTO request);

}
