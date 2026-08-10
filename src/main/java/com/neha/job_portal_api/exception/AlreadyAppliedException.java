package com.neha.job_portal_api.exception;

public class AlreadyAppliedException extends RuntimeException {

    public AlreadyAppliedException(String message) {
        super(message);
    }
}