package com.neha.job_portal_api.service;

import com.neha.job_portal_api.dto.ChangePasswordRequestDTO;
import com.neha.job_portal_api.dto.ForgotPasswordRequestDTO;
import com.neha.job_portal_api.dto.ResetPasswordRequestDTO;

public interface PasswordService {

    void changePassword(ChangePasswordRequestDTO request);

    void forgotPassword(ForgotPasswordRequestDTO request);

    void resetPassword(ResetPasswordRequestDTO request);
}