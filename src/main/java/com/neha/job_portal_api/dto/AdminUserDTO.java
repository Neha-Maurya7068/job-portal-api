package com.neha.job_portal_api.dto;

import com.neha.job_portal_api.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String location;

    private String skills;

    private String education;

    private Integer experience;

    private Role role;
}