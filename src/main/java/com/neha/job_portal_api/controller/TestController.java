package com.neha.job_portal_api.controller;

	import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.RestController;

	@RestController
	public class TestController {

	    @GetMapping("/api/test")
	    public String test() {
	        return "JWT Authentication Successful 🎉";
	    }
	        
	        
	        @GetMapping("/api/admin")
	        public String admin() {
	            return "Welcome Admin";
	        }

	        @GetMapping("/api/recruiter")
	        public String recruiter() {
	            return "Welcome Recruiter";
	        }

	        @GetMapping("/api/jobseeker")
	        public String jobSeeker() {
	            return "Welcome Job Seeker";

	    }
	        @GetMapping("/api/whoami")
	        public String whoAmI(Authentication authentication) {
	            return authentication.getName() + " : " + authentication.getAuthorities();
	        }
	}
	