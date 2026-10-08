package com.neha.job_portal_api.service;

import com.neha.job_portal_api.entity.User;

public interface AccountLockService {

    boolean isLocked(User user);

    void lockAccount(User user);

    void unlockIfExpired(User user);
}