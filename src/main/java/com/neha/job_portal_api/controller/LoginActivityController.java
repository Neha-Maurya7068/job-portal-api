package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.LoginActivityDTO;
import com.neha.job_portal_api.service.LoginActivityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/login-activity")
@RequiredArgsConstructor
public class LoginActivityController {

    private final LoginActivityService loginActivityService;

    @GetMapping
    public ResponseEntity<List<LoginActivityDTO>>
            getMyLoginActivities() {

        return ResponseEntity.ok(
                loginActivityService.getMyLoginActivities());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization")
            String authorizationHeader) {

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);

        loginActivityService.logoutCurrentSession(token);

        return ResponseEntity.ok(
                "Logged out successfully");
    }

    @PostMapping("/logout-all")
    public ResponseEntity<String> logoutAll() {

        loginActivityService.logoutAllSessions();

        return ResponseEntity.ok(
                "All sessions logged out successfully");
    }
}