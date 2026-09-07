package com.neha.job_portal_api.service;

import java.util.List;

import com.neha.job_portal_api.dto.AdminUserDTO;
import com.neha.job_portal_api.entity.Role;

public interface AdminUserService {

    // Get all users
    List<AdminUserDTO> getAllUsers();

    // Get user by ID
    AdminUserDTO getUserById(Long id);

    // Search users by name
    List<AdminUserDTO> searchUsersByName(String name);

    // Change user role
    AdminUserDTO updateUserRole(Long id, Role role);

    // Delete user
    void deleteUser(Long id);
}