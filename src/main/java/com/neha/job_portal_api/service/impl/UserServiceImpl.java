package com.neha.job_portal_api.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.neha.job_portal_api.dto.LoginRequestDTO;
import com.neha.job_portal_api.dto.RegisterRequestDTO;
import com.neha.job_portal_api.entity.Role;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.exception.EmailAlreadyExistsException;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.LoginActivityService;
import com.neha.job_portal_api.service.UserService;
import com.neha.job_portal_api.service.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final LoginActivityService loginActivityService;

    @Override
    public String registerUser(RegisterRequestDTO request) {

        if (userRepository.existsByEmail(
                request.getEmail())) {

            throw new EmailAlreadyExistsException(
                    "Email already exists"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(Role.JOB_SEEKER);

        userRepository.save(user);

        return "User Registered Successfully";
    }

    @Override
    public String loginUser(
            LoginRequestDTO request,
            String ipAddress,
            String userAgent) {

        Optional<User> optionalUser =
                userRepository.findByEmail(
                        request.getEmail()
                );

        if (optionalUser.isEmpty()) {
            return "Email not found";
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return "Invalid Password";
        }

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        // Record successful login
        loginActivityService.recordLogin(
                user,
                token,
                ipAddress,
                userAgent
        );

        return token;
    }
}