package com.neha.job_portal_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neha.job_portal_api.dto.RegisterRequestDTO;
import com.neha.job_portal_api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

	@PostMapping("/register")
	
	   public String registerUser(@RequestBody RegisterRequestDTO request) {

        return userService.registerUser(request);

    }
	
}
