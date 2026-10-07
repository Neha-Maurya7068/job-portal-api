package com.neha.job_portal_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.dto.LoginRequestDTO;
import com.neha.job_portal_api.dto.RegisterRequestDTO;
import com.neha.job_portal_api.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

	@PostMapping("/register")
	
	public String registerUser(@Valid @RequestBody RegisterRequestDTO request) {

        return userService.registerUser(request);

    }
	
	@PostMapping("/login")
	public ResponseEntity<String> login(
	        @RequestBody LoginRequestDTO request,
	        HttpServletRequest httpRequest) {

	    String ipAddress =
	            httpRequest.getHeader("X-Forwarded-For");

	    if (ipAddress == null || ipAddress.isBlank()) {
	        ipAddress =
	                httpRequest.getRemoteAddr();
	    }

	    String userAgent =
	            httpRequest.getHeader("User-Agent");

	    String token =
	            userService.loginUser(
	                    request,
	                    ipAddress,
	                    userAgent
	            );

	    return ResponseEntity.ok(token);
	}
}
