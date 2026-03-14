package com.revworkforce.service.impl;

import com.revworkforce.dto.request.LoginRequest;
import com.revworkforce.dto.response.AuthResponse;
import com.revworkforce.entity.Employee;
import com.revworkforce.exception.ResourceNotFoundException;
import com.revworkforce.repository.EmployeeRepository;
import com.revworkforce.security.JwtTokenProvider;
import com.revworkforce.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmployeeRepository employeeRepository;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getIdentifier(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AuthResponse authResponse = buildAuthResponse(employee);
        authResponse.setToken(jwt);
        return authResponse;
    }

    @Override
    public Employee getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }
        
        String currentPrincipalName = authentication.getName(); // Since Email is returned as username in UserPrincipal
        return employeeRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public AuthResponse getProfile() {
        return buildAuthResponse(getCurrentUser());
    }

    @Override
    public AuthResponse updateProfile(com.revworkforce.dto.request.ProfileUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Employee current = employeeRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        current.setPhone(request.getPhone());
        current.setAddress(request.getAddress());
        current.setEmergencyContact(request.getEmergencyContact());
        employeeRepository.save(current);
        return buildAuthResponse(current);
    }
    
    private AuthResponse buildAuthResponse(Employee employee) {
        return AuthResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .role(employee.getRole().name())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .emergencyContact(employee.getEmergencyContact())
                .build();
    }
}
