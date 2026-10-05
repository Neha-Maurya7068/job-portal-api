package com.neha.job_portal_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.neha.job_portal_api.dto.ChangePasswordRequestDTO;
import com.neha.job_portal_api.dto.ForgotPasswordRequestDTO;
import com.neha.job_portal_api.dto.ResetPasswordRequestDTO;
import com.neha.job_portal_api.service.PasswordService;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @PutMapping("/change")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request) {

        passwordService.changePassword(request);

        return ResponseEntity.ok(
                "Password changed successfully");
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request) {

        passwordService.forgotPassword(request);

        return ResponseEntity.ok(
                "If the email exists, a password reset link has been sent.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request) {

        passwordService.resetPassword(request);

        return ResponseEntity.ok(
                "Password reset successfully");
    }
}