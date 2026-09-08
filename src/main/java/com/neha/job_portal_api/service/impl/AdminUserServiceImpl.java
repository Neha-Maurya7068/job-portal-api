package com.neha.job_portal_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.job_portal_api.dto.AdminUserDTO;
import com.neha.job_portal_api.entity.Role;
import com.neha.job_portal_api.entity.User;
import com.neha.job_portal_api.exception.ResourceNotFoundException;
import com.neha.job_portal_api.repository.UserRepository;
import com.neha.job_portal_api.service.AdminUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    @Override
    public List<AdminUserDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public AdminUserDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );

        return convertToDTO(user);
    }

    @Override
    public List<AdminUserDTO> searchUsersByName(String name) {

        return userRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public AdminUserDTO updateUserRole(Long id, Role role) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );

        user.setRole(role);

        User updatedUser = userRepository.save(user);

        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );

        userRepository.delete(user);
    }

    private AdminUserDTO convertToDTO(User user) {

        AdminUserDTO dto = new AdminUserDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setLocation(user.getLocation());
        dto.setSkills(user.getSkills());
        dto.setEducation(user.getEducation());
        dto.setExperience(user.getExperience());
        dto.setRole(user.getRole());

        return dto;
    }
}