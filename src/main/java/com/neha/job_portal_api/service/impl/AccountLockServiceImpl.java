package com.neha.job_portal_api.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.AccountLockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountLockServiceImpl
        implements AccountLockService {

    private final UserRepository userRepository;

    private static final int LOCK_DURATION_MINUTES = 30;

    @Override
    public boolean isLocked(User user) {

        if (user.getLockedUntil() == null) {
            return false;
        }

        return user.getLockedUntil()
                .isAfter(LocalDateTime.now());
    }

    @Override
    public void lockAccount(User user) {

        LocalDateTime lockedUntil =
                LocalDateTime.now()
                        .plusMinutes(LOCK_DURATION_MINUTES);

        user.setLockedUntil(lockedUntil);

        userRepository.save(user);
    }

    @Override
    public void unlockIfExpired(User user) {

        if (user.getLockedUntil() != null
                && !user.getLockedUntil()
                        .isAfter(LocalDateTime.now())) {

            user.setLockedUntil(null);

            userRepository.save(user);
        }
    }
}