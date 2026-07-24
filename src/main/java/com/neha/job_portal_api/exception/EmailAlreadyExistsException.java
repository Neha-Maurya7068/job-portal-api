package com.neha.job_portal_api.exception;

public class EmailAlreadyExistsException extends RuntimeException {
	
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

}
