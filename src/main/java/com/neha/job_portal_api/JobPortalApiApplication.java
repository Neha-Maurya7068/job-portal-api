package com.neha.job_portal_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class JobPortalApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                JobPortalApiApplication.class,
                args);
    }
}