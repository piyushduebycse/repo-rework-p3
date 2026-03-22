package com.revworkforce.authemployee.service.impl;

import com.revworkforce.authemployee.entity.*;
import com.revworkforce.authemployee.repository.*;
import com.revworkforce.authemployee.service.AuthService;
import com.revworkforce.common.dto.request.LoginRequest;
import com.revworkforce.common.dto.request.ProfileUpdateRequest;
import com.revworkforce.common.dto.response.AuthResponse;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.common.security.JwtTokenProvider;
import com.revworkforce.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmployeeRepository employeeRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userPrincipal.getId());

        Employee employee = employeeRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        return AuthResponse.builder()
                .token(token)
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .role(employee.getRole().name())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .emergencyContact(employee.getEmergencyContact())
                .build();
    }

    @Override
    public Employee getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    @Override
    public AuthResponse getProfile() {
        Employee employee = getCurrentUser();
        return mapToAuthResponse(employee);
    }

    @Override
    @Transactional
    public AuthResponse updateProfile(ProfileUpdateRequest request) {
        Employee employee = getCurrentUser();

        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getAddress() != null) employee.setAddress(request.getAddress());
        if (request.getEmergencyContact() != null) employee.setEmergencyContact(request.getEmergencyContact());

        employee = employeeRepository.save(employee);
        return mapToAuthResponse(employee);
    }

    private AuthResponse mapToAuthResponse(Employee emp) {
        return AuthResponse.builder()
                .id(emp.getId())
                .employeeId(emp.getEmployeeId())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .email(emp.getEmail())
                .role(emp.getRole().name())
                .phone(emp.getPhone())
                .address(emp.getAddress())
                .emergencyContact(emp.getEmergencyContact())
                .build();
    }
}
