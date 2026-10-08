package com.neha.job_portal_api.entity;

public enum SecurityEventType {

    FAILED_LOGIN,

    MULTIPLE_FAILED_LOGINS,

    NEW_IP_LOGIN,

    NEW_DEVICE_LOGIN,
    
    ACCOUNT_LOCKED,

    NEW_IP_AND_DEVICE_LOGIN
}
