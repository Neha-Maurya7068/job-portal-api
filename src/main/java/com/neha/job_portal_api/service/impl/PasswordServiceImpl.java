package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.neha.job_portal_api.dto.ChangePasswordRequestDTO;
import com.neha.job_portal_api.dto.ForgotPasswordRequestDTO;
import com.neha.job_portal_api.dto.ResetPasswordRequestDTO;
import com.neha.job_portal_api.entity.PasswordResetToken;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.PasswordResetTokenRepository;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.EmailService;
import com.neha.job_portal_api.service.PasswordService;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 30;

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequestDTO request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Current password is incorrect");
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "New password must be different from current password");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO request) {

        /*
         * Security reason:
         * We do not reveal whether an email exists in the database.
         */

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return;
        }

        // Remove previous reset tokens
        passwordResetTokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setUsed(false);
        resetToken.setExpiryDate(
                LocalDateTime.now()
                        .plusMinutes(RESET_TOKEN_EXPIRATION_MINUTES));

        passwordResetTokenRepository.save(resetToken);

        String resetLink =
                "http://localhost:3000/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getName(),
                resetLink);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(request.getToken())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid password reset token"));

        if (resetToken.isUsed()) {
            throw new RuntimeException(
                    "Password reset token has already been used");
        }

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Password reset token has expired");
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}