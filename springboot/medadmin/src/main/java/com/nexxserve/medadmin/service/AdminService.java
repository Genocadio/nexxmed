// src/main/java/com/nexxserve/medadmin/service/AdminService.java
package com.nexxserve.medadmin.service;

import com.nexxserve.medadmin.dto.request.RegisterRequestDto;
import com.nexxserve.medadmin.dto.request.LoginRequestDto;
import com.nexxserve.medadmin.dto.response.RegisterResponseDto;
import com.nexxserve.medadmin.entity.Admins;
import com.nexxserve.medadmin.enums.Role;
import com.nexxserve.medadmin.repository.AdminRepository;
import com.nexxserve.medadmin.service.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegisterResponseDto register(RegisterRequestDto requestDto) {
        Admins admin = requestDto.toEntity();
        admin.setRole(Role.ADMIN);
        admin.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Admins savedAdmin = adminRepository.save(admin);
        String token = jwtService.generateAdminToken(savedAdmin.getId().toString(), List.of(Role.ADMIN.name()));
        return RegisterResponseDto.fromEntity(savedAdmin, token, "Registration successful");
    }

    public Optional<RegisterResponseDto> login(LoginRequestDto requestDto) {
        Optional<Admins> adminOpt = adminRepository.findByUsername(requestDto.getUsername());
        if (adminOpt.isPresent() && passwordEncoder.matches(requestDto.getPassword(), adminOpt.get().getPassword())) {
            Admins admin = adminOpt.get();
            String token = jwtService.generateAdminToken(admin.getId().toString(), List.of(Role.ADMIN.name()));
            return Optional.of(RegisterResponseDto.fromEntity(admin, token, "Login successful"));
        }
        return Optional.empty();
    }

    public Optional<Admins> findById(Long id) {
        return adminRepository.findById(id);
    }
}