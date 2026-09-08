package com.neha.job_portal_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.neha.job_portal_api.dto.AdminUserDTO;
import com.neha.job_portal_api.dto.ApiResponse;
import com.neha.job_portal_api.entity.Role;
import com.neha.job_portal_api.service.AdminUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // Get all users
    @GetMapping
    public ApiResponse<List<AdminUserDTO>> getAllUsers() {

        List<AdminUserDTO> users =
                adminUserService.getAllUsers();

        return new ApiResponse<>(
                true,
                "Users fetched successfully",
                users
        );
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ApiResponse<AdminUserDTO> getUserById(
            @PathVariable Long id) {

        AdminUserDTO user =
                adminUserService.getUserById(id);

        return new ApiResponse<>(
                true,
                "User fetched successfully",
                user
        );
    }

    // Search users by name
    @GetMapping("/search")
    public ApiResponse<List<AdminUserDTO>> searchUsersByName(
            @RequestParam String name) {

        List<AdminUserDTO> users =
                adminUserService.searchUsersByName(name);

        return new ApiResponse<>(
                true,
                "Users found successfully",
                users
        );
    }

    // Update user role
    @PutMapping("/{id}/role")
    public ApiResponse<AdminUserDTO> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role role) {

        AdminUserDTO updatedUser =
                adminUserService.updateUserRole(id, role);

        return new ApiResponse<>(
                true,
                "User role updated successfully",
                updatedUser
        );
    }

    // Delete user
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable Long id) {

        adminUserService.deleteUser(id);
    }
}